package io.github.mangi.eta.data.repository

import android.content.Context
import io.github.mangi.eta.agent.terminal.TerminalRuntime
import io.github.mangi.eta.data.db.EtaDatabase
import io.github.mangi.eta.data.db.ProjectSpaceEntity
import io.github.mangi.eta.data.db.ProjectSpaceKind
import java.util.UUID

/**
 * 项目工作区在运行期的只读快照。
 *
 * 工具调用点大多不在挂起上下文里，解析必须能同步完成，所以运行期只读这份不可变快照；
 * 数据库实体不进热路径，避免持锁引用 Room 对象。
 */
internal data class ProjectSpaceRef(
    val spaceKey: String,
    val title: String,
    val kind: ProjectSpaceKind,
    val hostPath: String,
    val linuxPath: String,
    val deletable: Boolean,
)

/**
 * 项目（工作区）注册表。
 *
 * 身份只影响**默认工作区**的宿主路径：root 身份写 `/data/local/tmp/eta`，
 * 普通应用身份写 `filesDir/…/workspace`（`/data/local/tmp` 对普通身份不可写）。
 * 因此默认工作区的 hostPath 不信任库里的值，每次解析按当前身份重算；
 * 其它项目的 hostPath 是用户选的绝对路径，按库里的值返回。
 */
internal object ProjectSpaceRepository {

    @Volatile private var application: Context? = null

    @Volatile private var cachedSpaces: List<ProjectSpaceRef> = emptyList()

    fun init(context: Context) {
        application = context.applicationContext
        cachedSpaces = emptyList()
    }

    fun cached(): List<ProjectSpaceRef> = cachedSpaces

    /**
     * 解析会话所属项目。`spaceKey` 为 null 视为默认工作区，与「未归属会话」行为一致，
     * 这是 P0 阶段零迁移的前提：现有会话不写 `space_key` 也不会改变落点。
     */
    fun resolve(spaceKey: String?, identity: String): ProjectSpaceRef {
        val key = spaceKey?.takeIf(String::isNotBlank) ?: ProjectSpaceEntity.DEFAULT_SPACE_KEY
        val hit = cachedSpaces.firstOrNull { it.spaceKey == key }
        if (hit == null) return defaultRef(identity)
        return if (hit.kind == ProjectSpaceKind.BUILTIN) {
            hit.copy(hostPath = TerminalRuntime.workspace(identity))
        } else {
            hit
        }
    }

    suspend fun reload(): List<ProjectSpaceRef> {
        val dao = database()?.projectSpaceDao() ?: return cachedSpaces
        cachedSpaces = dao.spaces().map(::toRef)
        return cachedSpaces
    }

    /** 内置默认工作区始终存在；缺失时补一行，已存在则不动（避免覆盖用户改过的标题）。 */
    suspend fun ensureDefault(identity: String): ProjectSpaceRef {
        val dao = database()?.projectSpaceDao() ?: return defaultRef(identity)
        if (dao.space(ProjectSpaceEntity.DEFAULT_SPACE_KEY) == null) {
            val now = System.currentTimeMillis()
            dao.upsert(
                ProjectSpaceEntity(
                    id = ProjectSpaceEntity.DEFAULT_SPACE_ID,
                    spaceKey = ProjectSpaceEntity.DEFAULT_SPACE_KEY,
                    kind = ProjectSpaceKind.BUILTIN,
                    hostPath = TerminalRuntime.workspace(identity),
                    linuxPath = ProjectSpaceEntity.DEFAULT_LINUX_PATH,
                    title = ProjectSpaceEntity.DEFAULT_TITLE,
                    deletable = false,
                    lastUsedAt = now,
                    createdAt = now,
                ),
            )
        }
        reload()
        return resolve(ProjectSpaceEntity.DEFAULT_SPACE_KEY, identity)
    }

    /**
     * 重命名项目：只改标题，不动磁盘路径。
     *
     * 标题是给人看的标签，路径是身份与目录事实；改名若跟着挪目录，
     * 已经写进会话里的 cwd、终端里开着的 shell 全部会漂。内置工作区不可改名。
     */
    suspend fun rename(spaceKey: String, title: String): Boolean {
        val dao = database()?.projectSpaceDao() ?: return false
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return false
        val entity = dao.space(spaceKey) ?: return false
        if (entity.kind == ProjectSpaceKind.BUILTIN) return false
        if (entity.title == trimmed) return true
        dao.upsert(entity.copy(title = trimmed))
        reload()
        return true
    }

    /** 私有项目：宿主目录在默认工作区之下，Linux 侧是 `/workspace/projects/<slug>` 的子路径。 */
    suspend fun createPrivate(title: String, identity: String): ProjectSpaceRef? {
        val dao = database()?.projectSpaceDao() ?: return null
        val trimmed = title.trim().ifEmpty { ProjectSpaceEntity.DEFAULT_TITLE }
        val id = UUID.randomUUID().toString()
        val slug = slugify(trimmed)
        val hostPath = privateHostPath(TerminalRuntime.workspace(identity), slug, id)
        val now = System.currentTimeMillis()
        runCatching { java.io.File(hostPath).mkdirs() }
        dao.upsert(
            ProjectSpaceEntity(
                id = id,
                spaceKey = "space:$id",
                kind = ProjectSpaceKind.PRIVATE,
                hostPath = hostPath,
                linuxPath = privateLinuxPath(slug),
                title = trimmed,
                deletable = true,
                lastUsedAt = now,
                createdAt = now,
            ),
        )
        reload()
        return resolve("space:$id", identity)
    }

    suspend fun touch(spaceKey: String) {
        database()?.projectSpaceDao()?.touch(spaceKey, System.currentTimeMillis())
    }

    suspend fun delete(spaceKey: String): Boolean {
        val removed = database()?.projectSpaceDao()?.deleteDeletable(spaceKey) ?: 0
        if (removed > 0) reload()
        return removed > 0
    }

    private fun database(): EtaDatabase? = application?.let { EtaDatabase.get(it) }

    private fun defaultRef(identity: String): ProjectSpaceRef = ProjectSpaceRef(
        spaceKey = ProjectSpaceEntity.DEFAULT_SPACE_KEY,
        title = ProjectSpaceEntity.DEFAULT_TITLE,
        kind = ProjectSpaceKind.BUILTIN,
        hostPath = TerminalRuntime.workspace(identity),
        linuxPath = ProjectSpaceEntity.DEFAULT_LINUX_PATH,
        deletable = false,
    )

    private fun toRef(entity: ProjectSpaceEntity): ProjectSpaceRef = ProjectSpaceRef(
        spaceKey = entity.spaceKey,
        title = entity.title,
        kind = entity.kind,
        hostPath = entity.hostPath,
        linuxPath = entity.linuxPath,
        deletable = entity.deletable,
    )
}

/** 目录名只保留可安全出现在路径与 git 仓库里的字符，其余折叠成单个连字符。 */
internal fun slugify(title: String): String {
    val slug = title.lowercase()
        .map { char -> if (char.isLetterOrDigit() && char.code < 128) char else '-' }
        .joinToString("")
        .trim('-')
        .replace(Regex("-{2,}"), "-")
        .take(32)
        .trim('-')
    return slug.ifEmpty { "project" }
}

internal fun privateHostPath(rootPath: String, slug: String, id: String): String =
    "${rootPath.trimEnd('/')}/projects/$slug-${id.take(8)}"

internal fun privateLinuxPath(slug: String): String = "/workspace/projects/$slug"

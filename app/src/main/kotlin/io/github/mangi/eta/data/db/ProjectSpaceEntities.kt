package io.github.mangi.eta.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** 项目空间的种类：决定路径由谁提供、能否删除。 */
internal enum class ProjectSpaceKind {
    /** 内置默认工作区，宿主路径固定为 `/data/local/tmp/eta`。 */
    BUILTIN,

    /** 托管项目，宿主路径位于内置工作区的 `projects/` 之下。 */
    PRIVATE,

    /** 外部目录，复用 [io.github.mangi.eta.agent.terminal.SharedFolderMounts] 的 bind。 */
    EXTERNAL,
}

/**
 * 一个项目空间 = 宿主路径 + rootfs 内路径 + 展示名。
 *
 * 会话通过 [spaceKey] 引用它；[hostPath] 是真实目录，root 直接访问，不经过 SAF。
 */
@Serializable
@Entity(
    tableName = "project_spaces",
    indices = [Index(value = ["space_key"], unique = true)],
)
internal data class ProjectSpaceEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "space_key") val spaceKey: String,
    val kind: ProjectSpaceKind,
    @ColumnInfo(name = "host_path") val hostPath: String,
    @ColumnInfo(name = "linux_path") val linuxPath: String,
    val title: String,
    val deletable: Boolean = true,
    @ColumnInfo(name = "last_used_at") val lastUsedAt: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
) {
    companion object {
        /** 内置默认工作区的稳定键，迁库时写入。 */
        const val DEFAULT_SPACE_KEY = "space:default"

        const val DEFAULT_SPACE_ID = "space-default"

        const val DEFAULT_TITLE = "默认工作区"

        const val DEFAULT_HOST_PATH = "/data/local/tmp/eta"

        const val DEFAULT_LINUX_PATH = "/workspace"
    }
}

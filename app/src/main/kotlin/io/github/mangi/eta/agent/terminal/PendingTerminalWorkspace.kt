package io.github.mangi.eta.agent.terminal

/**
 * 「在项目里打开终端」的一次性交接。
 *
 * UI 先把项目路径放进来，终端建会话时消费一次：宿主路径当 bind 源，rootfs 内路径当初始 cwd。
 * 用一次性值而不是全局设置，是为了不污染之后用户手动开的普通会话。
 */
internal object PendingTerminalWorkspace {
    internal data class Workspace(val hostPath: String, val linuxPath: String?)

    @Volatile
    private var pending: Workspace? = null

    fun request(hostPath: String, linuxPath: String?) {
        val host = hostPath.trim()
        pending = if (host.isEmpty()) null else Workspace(host, linuxPath?.takeIf { it.isNotBlank() })
    }

    fun peek(): Workspace? = pending

    fun consume(): Workspace? {
        val value = pending
        pending = null
        return value
    }
}

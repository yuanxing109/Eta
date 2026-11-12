package io.github.mangi.eta.agent.terminal

/**
 * 「在项目里打开终端」的一次性握手槽。
 *
 * UI 侧发起该动作时先把项目的宿主绝对路径 [request] 暂存到这里；控制台首次建会话时
 * [consume] 读取一次并清空，随后作为该会话的初始工作目录与挂载源传入
 * [ConsoleSessionController.open]（linuxHostWorkspacePath / linuxDefaultCwd）。
 * 只消费一次，避免后续普通新建终端被上一次的项目路径污染。
 */
internal object PendingTerminalWorkspace {
    @Volatile private var hostPath: String? = null

    fun request(path: String?) {
        hostPath = path?.takeIf { it.isNotBlank() }
    }

    fun consume(): String? = hostPath.also { hostPath = null }
}

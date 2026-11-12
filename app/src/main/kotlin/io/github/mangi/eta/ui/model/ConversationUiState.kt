package io.github.mangi.eta.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class ConversationPaneUiState(
    val conversations: List<ConversationSummaryUi>,
    val selectedConversationId: String?,
    val searchQuery: String,
    /** 侧栏一级分组：项目（工作区）。默认工作区不在其中，会话仍按时间分组展示。 */
    val spaces: List<ConversationSpaceUi> = emptyList(),
)

/** 侧栏分组头所需的项目信息，只投影展示字段，不带宿主路径。 */
@Immutable
data class ConversationSpaceUi(
    val spaceKey: String,
    val title: String,
)

@Immutable
data class ConversationSummaryUi(
    val id: String,
    val title: String,
    val preview: String,
    val timeLabel: String,
    val updatedAtMillis: Long = 0L,
    val mode: ConversationModeUi,
    val isPinned: Boolean = false,
    val isActiveRun: Boolean = false,
    val runStatus: String? = null,
    val characterName: String? = null,
    val characterAvatarPath: String? = null,
    /** 所属项目空间；null 或默认工作区时按时间分组，与历史行为一致。 */
    val spaceKey: String? = null,
)

@Immutable
enum class ConversationModeUi {
    Chat,
    PhoneAgent,
    Terminal,
    Automation,
}

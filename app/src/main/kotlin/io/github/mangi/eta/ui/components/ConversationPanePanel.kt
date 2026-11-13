package io.github.mangi.eta.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.backdrop.couiBackdrop
import com.itos.coui.backdrop.rememberCouiBackdrop
import com.itos.coui.dialog.CouiDialogDefaults
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.icons.CouiIcons
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.layout.Divider as CouiDivider
import com.itos.coui.menu.CouiMenuItem
import com.itos.coui.menu.Menu
import com.itos.coui.menu.couiMenuAnchor
import com.itos.coui.menu.rememberCouiMenuAnchor
import com.itos.coui.shape.CouiShape
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text as CouiText
import com.itos.coui.theme.CouiTheme
import io.github.mangi.eta.R
import io.github.mangi.eta.data.model.AppearanceUiStyle
import io.github.mangi.eta.ui.app.LocalUiStyle
import io.github.mangi.eta.ui.components.coui.CouiIcon
import io.github.mangi.eta.ui.model.ConversationPaneUiState
import io.github.mangi.eta.ui.model.ConversationSummaryUi
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.DropdownDefaults
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowListPopup
import androidx.compose.material.icons.outlined.Folder
import io.github.mangi.eta.ui.model.ConversationSpaceUi

@Stable
private class PaneBarInsets {
    var top by mutableStateOf(0.dp)
    var bottom by mutableStateOf(0.dp)
}

@Composable
internal fun ConversationPanePanel(
    state: ConversationPaneUiState,
    width: Dp,
    listState: LazyListState,
    isRevealed: Boolean,
    activeModelLabel: String?,
    hasPermissionAttention: Boolean,
    onSearchChange: (String) -> Unit,
    onNewConversation: () -> Unit,
    onConversationSelected: (String) -> Unit,
    onConversationTogglePin: (ConversationSummaryUi) -> Unit,
    onConversationRename: (ConversationSummaryUi) -> Unit,
    onConversationExport: (ConversationSummaryUi) -> Unit,
    onConversationDelete: (ConversationSummaryUi) -> Unit,
    onConversationMoveToSpace: (ConversationSummaryUi) -> Unit = {},
    onNewConversationInSpace: (ConversationSpaceUi) -> Unit = {},
    onOpenProjectInTerminal: (ConversationSpaceUi) -> Unit = {},
    onRenameProjectSpace: (ConversationSpaceUi) -> Unit = {},
    onDeleteProjectSpace: (ConversationSpaceUi) -> Unit = {},
    onCreateProject: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onOpenModelProviders: () -> Unit,
    onOpenTools: () -> Unit,
    onOpenSkills: () -> Unit,
    onOpenCharacters: () -> Unit,
    onOpenPermissions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // state.conversations 已由 AgentAppState 按标题、预览与消息内容过滤。
    val query = state.searchQuery.trim()
    val isSearching = query.isNotBlank()
    var expandedSpaceKeys by remember { mutableStateOf(setOf<String>()) }
    var collapsedSpaceKeys by remember { mutableStateOf(setOf<String>()) }
    val entries = remember(
        state.conversations,
        state.spaces,
        expandedSpaceKeys,
        collapsedSpaceKeys,
        query,
    ) {
        buildDrawerEntries(state.conversations, state.spaces, expandedSpaceKeys, collapsedSpaceKeys, query)
    }
    val containerColor = conversationPaneContainerColor()
    val isCoui = LocalUiStyle.current == AppearanceUiStyle.COUI
    val couiBackdrop = if (isCoui) rememberCouiBackdrop() else null
    val miuixBackdrop = if (!isCoui) rememberTopBarBackdrop() else null
    val barInsets = remember { PaneBarInsets() }
    val bodyCaptureModifier = when {
        isCoui && couiBackdrop != null -> Modifier.couiBackdrop(couiBackdrop)
        !isCoui && miuixBackdrop != null -> Modifier.captureForTopBar(miuixBackdrop)
        else -> Modifier
    }

    Box(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .background(containerColor),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 测量顶栏与底栏真实高度（含安全区与 IME），同步给滚动画布的 contentPadding，
            // 使列表自然穿梭于顶底浮层之下并分别被各自 backdrop 采样，同时避免 Subcompose 重组开销。
            Layout(
                modifier = Modifier.fillMaxSize(),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(containerColor)
                            .then(bodyCaptureModifier),
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds()
                                .windowInsetsPadding(
                                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                                )
                                .scrollEndHaptic()
                                .overScrollVertical(),
                            contentPadding = PaddingValues(
                                top = barInsets.top + 10.dp,
                                bottom = barInsets.bottom + 10.dp,
                            ),
                            verticalArrangement = Arrangement.spacedBy(ConversationPanelMetrics.RowGap),
                            overscrollEffect = null,
                        ) {
                            if (!isSearching) {
                                item(key = "pane_workbench") {
                                    PaneWorkbenchSection(
                                        activeModelLabel = activeModelLabel,
                                        onOpenModelProviders = onOpenModelProviders,
                                        onOpenCharacters = onOpenCharacters,
                                        onOpenTools = onOpenTools,
                                        onOpenSkills = onOpenSkills,
                                    )
                                }
                            }

                            if (entries.isEmpty()) {
                                item(key = "pane_empty_conversations") {
                                    EmptyConversations(isSearching = isSearching)
                                }
                            } else {
                                entries.forEachIndexed { entryIndex, entry ->
                                    when (entry) {
                                        is ConversationDrawerEntry.SpaceHeader -> item(
                                            key = "space-${entry.space.spaceKey}",
                                        ) {
                                            ConversationSpaceHeader(
                                                header = entry,
                                                isFirstSection = entryIndex == 0,
                                                onToggle = {
                                                    if (entry.expanded) {
                                                        expandedSpaceKeys = expandedSpaceKeys - entry.space.spaceKey
                                                        collapsedSpaceKeys = collapsedSpaceKeys + entry.space.spaceKey
                                                    } else {
                                                        expandedSpaceKeys = expandedSpaceKeys + entry.space.spaceKey
                                                        collapsedSpaceKeys = collapsedSpaceKeys - entry.space.spaceKey
                                                    }
                                                },
                                                onNewConversation = { onNewConversationInSpace(entry.space) },
                                                onCreateProject = onCreateProject,
                                                onOpenInTerminal = { onOpenProjectInTerminal(entry.space) },
                                                onRename = { onRenameProjectSpace(entry.space) },
                                                onDelete = { onDeleteProjectSpace(entry.space) },
                                            )
                                        }
                                        is ConversationDrawerEntry.Conversation -> item(key = entry.conversation.id) {
                                            val conversation = entry.conversation
                                            Box(
                                                modifier = if (entry.nested) {
                                                    Modifier.padding(start = ConversationNestedRowIndent)
                                                } else {
                                                    Modifier
                                                },
                                            ) {
                                                ConversationHistoryRow(
                                                    conversation = conversation,
                                                    selected = conversation.id == state.selectedConversationId,
                                                    onClick = { onConversationSelected(conversation.id) },
                                                    onTogglePin = { onConversationTogglePin(conversation) },
                                                    onRename = { onConversationRename(conversation) },
                                                    onExport = { onConversationExport(conversation) },
                                                    onDelete = { onConversationDelete(conversation) },
                                                    onMoveToSpace = { onConversationMoveToSpace(conversation) },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ConversationPaneTopBar(
                        containerColor = containerColor,
                        couiBackdrop = couiBackdrop,
                        miuixBackdrop = miuixBackdrop,
                        onNewConversation = onNewConversation,
                    )
                    ConversationPaneBottomDock(
                        query = state.searchQuery,
                        isRevealed = isRevealed,
                        hasPermissionAttention = hasPermissionAttention,
                        containerColor = containerColor,
                        couiBackdrop = couiBackdrop,
                        miuixBackdrop = miuixBackdrop,
                        onSearchChange = onSearchChange,
                        onOpenPermissions = onOpenPermissions,
                        onOpenSettings = onOpenSettings,
                    )
                },
            ) { measurables, constraints ->
                val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
                val topBar = measurables[1].measure(looseConstraints)
                val bottomDock = measurables[2].measure(looseConstraints)
                val topDp = topBar.height.toDp()
                val bottomDp = bottomDock.height.toDp()
                if (barInsets.top != topDp) barInsets.top = topDp
                if (barInsets.bottom != bottomDp) barInsets.bottom = bottomDp
                val body = measurables[0].measure(constraints)
                layout(constraints.maxWidth, constraints.maxHeight) {
                    body.placeRelative(0, 0)
                    topBar.placeRelative(0, 0)
                    bottomDock.placeRelative(0, constraints.maxHeight - bottomDock.height)
                }
            }

            // 侧栏物理右侧细微收边，保证浅/深/纯黑模式下与右侧聊天舞台始终保持利落边界。
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(0.5.dp)
                    .background(conversationPaneEdgeBorderColor()),
            )
        }
    }
}

private data class WorkbenchEntry(
    val icon: ImageVector,
    val title: String,
    val trailingValue: String? = null,
    val onClick: () -> Unit,
)

@Composable
private fun PaneWorkbenchSection(
    activeModelLabel: String?,
    onOpenModelProviders: () -> Unit,
    onOpenCharacters: () -> Unit,
    onOpenTools: () -> Unit,
    onOpenSkills: () -> Unit,
) {
    val modelSummary = activeModelLabel?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.conversation_model_unconfigured)
    val entries = listOf(
        WorkbenchEntry(
            icon = ConversationPaneIcons.Model,
            title = stringResource(R.string.conversation_dock_models),
            trailingValue = modelSummary,
            onClick = onOpenModelProviders,
        ),
        WorkbenchEntry(
            icon = ConversationPaneIcons.Character,
            title = stringResource(R.string.conversation_dock_characters),
            onClick = onOpenCharacters,
        ),
        WorkbenchEntry(
            icon = ConversationPaneIcons.Tools,
            title = stringResource(R.string.route_tools),
            onClick = onOpenTools,
        ),
        WorkbenchEntry(
            icon = ConversationPaneIcons.Skills,
            title = stringResource(R.string.route_skills),
            onClick = onOpenSkills,
        ),
    )

    val cardColor = conversationPaneCardColor()
    val dividerIndent = ConversationPanelMetrics.WorkbenchRowHorizontalPadding +
        ConversationPanelMetrics.WorkbenchIconSize +
        ConversationPanelMetrics.WorkbenchIconGap

    when (LocalUiStyle.current) {
        AppearanceUiStyle.COUI -> {
            val shape = remember { CouiShape(ConversationPanelMetrics.WorkbenchCornerRadius, 2.5f) }
            val dark = CouiTheme.isDark
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ConversationPanelMetrics.PaneHorizontalPadding)
                    .then(
                        if (!dark) {
                            Modifier.dropShadow(
                                shape = shape,
                                shadow = Shadow(
                                    radius = 10.dp,
                                    color = Color.Black,
                                    alpha = 0.025f,
                                ),
                            )
                        } else {
                            Modifier
                        },
                    )
                    .clip(shape)
                    .background(cardColor, shape)
                    .then(
                        if (!dark) {
                            Modifier.border(0.5.dp, Color(0x0A000000), shape)
                        } else {
                            Modifier.border(0.5.dp, Color(0x12FFFFFF), shape)
                        },
                    ),
            ) {
                entries.forEachIndexed { index, entry ->
                    CouiWorkbenchRow(entry = entry, dark = dark)
                    if (index < entries.lastIndex) {
                        CouiDivider(
                            startIndent = dividerIndent,
                            endIndent = ConversationPanelMetrics.WorkbenchRowHorizontalPadding,
                        )
                    }
                }
            }
        }
        AppearanceUiStyle.MIUIX -> {
            val colors = MiuixTheme.colorScheme
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ConversationPanelMetrics.PaneHorizontalPadding),
                colors = CardDefaults.defaultColors(color = cardColor),
            ) {
                entries.forEachIndexed { index, entry ->
                    MiuixWorkbenchRow(entry = entry)
                    if (index < entries.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                start = dividerIndent,
                                end = ConversationPanelMetrics.WorkbenchRowHorizontalPadding,
                            ),
                            thickness = 0.5.dp,
                            color = colors.dividerLine.copy(alpha = 0.45f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CouiWorkbenchRow(
    entry: WorkbenchEntry,
    dark: Boolean,
) {
    val colors = CouiPageDefaults.themeColors(dark)
    val source = remember { MutableInteractionSource() }
    val chevronTint = Color(if (dark) 0x4DFFFFFF else 0x42000000)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = ConversationPanelMetrics.WorkbenchRowMinHeight)
            .couiButtonFeedback(dark, source, enabled = true, list = true)
            .clickable(
                interactionSource = source,
                indication = null,
                role = Role.Button,
                onClick = entry.onClick,
            )
            .padding(
                horizontal = ConversationPanelMetrics.WorkbenchRowHorizontalPadding,
                vertical = 11.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CouiIcon(
            imageVector = entry.icon,
            contentDescription = null,
            modifier = Modifier.size(ConversationPanelMetrics.WorkbenchIconSize),
            tint = colors.title.copy(alpha = 0.86f),
        )
        Spacer(Modifier.width(ConversationPanelMetrics.WorkbenchIconGap))
        CouiText(
            text = entry.title,
            kind = CouiTextKind.ItemTitle,
            color = colors.title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (!entry.trailingValue.isNullOrEmpty()) {
            CouiText(
                text = entry.trailingValue,
                kind = CouiTextKind.ItemValue,
                color = colors.summary,
                modifier = Modifier
                    .widthIn(max = 136.dp)
                    .padding(start = 8.dp),
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        CouiIcon(
            imageVector = CouiIcons.Outlined.Next,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 6.dp)
                .size(width = 11.dp, height = 20.dp),
            tint = chevronTint,
        )
    }
}

@Composable
private fun MiuixWorkbenchRow(entry: WorkbenchEntry) {
    val colors = MiuixTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = ConversationPanelMetrics.WorkbenchRowMinHeight)
            .clickable(role = Role.Button, onClick = entry.onClick)
            .padding(
                horizontal = ConversationPanelMetrics.WorkbenchRowHorizontalPadding,
                vertical = 11.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = entry.icon,
            contentDescription = null,
            modifier = Modifier.size(ConversationPanelMetrics.WorkbenchIconSize),
            tint = colors.onSurface.copy(alpha = 0.85f),
        )
        Spacer(Modifier.width(ConversationPanelMetrics.WorkbenchIconGap))
        Text(
            text = entry.title,
            style = MiuixTheme.textStyles.body1.copy(fontSize = 15.sp),
            color = colors.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (!entry.trailingValue.isNullOrEmpty()) {
            Text(
                text = entry.trailingValue,
                style = MiuixTheme.textStyles.footnote1,
                color = colors.onSurfaceVariantSummary,
                modifier = Modifier
                    .widthIn(max = 136.dp)
                    .padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = ConversationPaneIcons.ChevronRight,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(14.dp),
            tint = colors.onSurfaceVariantActions,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ConversationHistoryRow(
    conversation: ConversationSummaryUi,
    selected: Boolean,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onRename: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit,
    onMoveToSpace: () -> Unit = {},
) {
    var showActionMenu by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val anchor = rememberCouiMenuAnchor()
    val isCoui = LocalUiStyle.current == AppearanceUiStyle.COUI
    val couiDark = CouiTheme.isDark
    val couiRowShape = remember { CouiShape(ConversationPanelMetrics.RowCornerRadius, 2.1f) }
    val miuixColors = MiuixTheme.colorScheme
    val miuixDark = miuixColors.surface.luminance() < 0.5f

    val rowBackground = when {
        !selected -> Color.Transparent
        isCoui -> if (couiDark) Color(0xFF262932) else Color.White
        else -> miuixColors.tertiaryContainer.copy(alpha = if (miuixDark) 0.72f else 0.88f)
    }
    val rowModifier = if (isCoui) {
        val source = remember { MutableInteractionSource() }
        Modifier
            .then(
                if (selected && !couiDark) {
                    Modifier.dropShadow(
                        shape = couiRowShape,
                        shadow = Shadow(
                            radius = 8.dp,
                            color = Color.Black,
                            alpha = 0.028f,
                        ),
                    )
                } else {
                    Modifier
                },
            )
            .clip(couiRowShape)
            .background(rowBackground, couiRowShape)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 0.5.dp,
                        color = if (couiDark) Color(0x1AFFFFFF) else Color(0x0D000000),
                        shape = couiRowShape,
                    )
                } else {
                    Modifier
                },
            )
            .couiButtonFeedback(couiDark, source, enabled = true, list = true)
    } else {
        Modifier.squircleSurface(
            color = rowBackground,
            cornerRadius = ConversationPanelMetrics.RowCornerRadius,
        )
    }

    val titleColor = if (isCoui) {
        CouiPageDefaults.themeColors(couiDark).title
    } else {
        miuixColors.onSurface
    }
    val summaryColor = if (isCoui) {
        CouiPageDefaults.themeColors(couiDark).summary
    } else {
        miuixColors.onSurfaceVariantSummary
    }
    val accentColor = if (isCoui) {
        CouiTheme.colorSchemeFor(couiDark).accent
    } else {
        miuixColors.primary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ConversationPanelMetrics.PaneHorizontalPadding),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = ConversationPanelMetrics.RowMinHeight)
                .couiMenuAnchor(anchor)
                .then(rowModifier)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        anchor.open { showActionMenu = true }
                    },
                )
                .padding(
                    start = ConversationPanelMetrics.RowHorizontalPadding,
                    end = if (selected) 8.dp else ConversationPanelMetrics.RowHorizontalPadding,
                    top = ConversationPanelMetrics.RowVerticalPadding,
                    bottom = ConversationPanelMetrics.RowVerticalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (conversation.characterName != null) {
                CharacterAvatar(
                    name = conversation.characterName,
                    avatarPath = conversation.characterAvatarPath,
                    size = 30.dp,
                    shape = CircleShape,
                    modifier = if (selected) {
                        Modifier.border(1.5.dp, accentColor.copy(alpha = 0.75f), CircleShape)
                    } else {
                        Modifier
                    },
                )
                Spacer(Modifier.width(11.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                val title = conversation.title.ifBlank { conversation.preview }
                ConversationSpaceRowTitle(
                    text = title,
                    color = titleColor,
                    emphasized = selected,
                )
                // 标题与角色名相同（如未改名的角色会话）时不再重复第二行。
                conversation.characterName?.takeIf { it != title }?.let { name ->
                    ConversationSpaceRowCaption(
                        text = name,
                        color = summaryColor,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                conversation.runStatus?.let { status ->
                    ConversationSpaceRowCaption(
                        text = status,
                        color = accentColor,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            if (conversation.isActiveRun) {
                Box(
                    modifier = Modifier
                        .padding(start = ConversationPanelMetrics.ActiveDotGap)
                        .size(ConversationPanelMetrics.ActiveDotSize)
                        .clip(CircleShape)
                        .background(accentColor),
                )
            } else if (conversation.isPinned) {
                Icon(
                    imageVector = ConversationPaneIcons.Pin,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(13.dp),
                    tint = summaryColor.copy(alpha = 0.72f),
                )
            }

            if (selected) {
                val moreLabel = stringResource(R.string.action_more)
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = moreLabel,
                        ) {
                            anchor.open { showActionMenu = true }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = moreLabel,
                        modifier = Modifier.size(17.dp),
                        tint = summaryColor,
                    )
                }
            }
        }

        // 将选中指示条放在左内边距留白区，保证选中行与普通行标题严格处于同一条左对齐基线。
        if (selected && conversation.characterName == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 5.5.dp)
                    .width(2.5.dp)
                    .height(16.dp)
                    .clip(CircleShape)
                    .background(accentColor),
            )
        }

        val pinText = stringResource(
            if (conversation.isPinned) {
                R.string.action_unpin_conversation
            } else {
                R.string.action_pin_conversation
            },
        )
        val renameText = stringResource(R.string.action_rename)
        val exportText = stringResource(R.string.action_export)
        val moveSpaceText = stringResource(R.string.action_move_to_space)
        val deleteText = stringResource(R.string.action_delete)
        when (LocalUiStyle.current) {
            AppearanceUiStyle.COUI -> {
                val destructiveColor = CouiDialogDefaults.themeColors().destructive
                val menuItems = remember(pinText, renameText, moveSpaceText, exportText, deleteText, destructiveColor) {
                    listOf(
                        CouiMenuItem(value = 0, text = pinText, icon = Icons.Outlined.PushPin),
                        CouiMenuItem(value = 1, text = renameText, icon = Icons.Outlined.Edit),
                        CouiMenuItem(value = 2, text = moveSpaceText, icon = Icons.Outlined.Folder),
                        CouiMenuItem(value = 3, text = exportText, icon = Icons.Outlined.Download),
                        CouiMenuItem(
                            value = 4,
                            text = deleteText,
                            icon = Icons.Outlined.Delete,
                            iconTint = destructiveColor,
                            dividerBefore = true,
                        ),
                    )
                }
                Menu(
                    expanded = showActionMenu,
                    onDismissRequest = { showActionMenu = false },
                    items = menuItems,
                    onItemClick = { item ->
                        showActionMenu = false
                        when (item.value) {
                            0 -> onTogglePin()
                            1 -> onRename()
                            2 -> onMoveToSpace()
                            3 -> onExport()
                            else -> onDelete()
                        }
                    },
                    anchor = anchor,
                )
            }
            AppearanceUiStyle.MIUIX -> {
                WindowListPopup(
                    show = showActionMenu,
                    popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                    alignment = PopupPositionProvider.Align.BottomEnd,
                    onDismissRequest = { showActionMenu = false },
                ) {
                    val pinItem = remember(pinText) {
                        DropdownItem(
                            text = pinText,
                            icon = { modifier ->
                                Icon(
                                    imageVector = Icons.Outlined.PushPin,
                                    contentDescription = null,
                                    modifier = modifier.size(ConversationPanelMetrics.ActionIconSize),
                                )
                            },
                        )
                    }
                    val renameItem = remember(renameText) {
                        DropdownItem(
                            text = renameText,
                            icon = { modifier ->
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    modifier = modifier.size(ConversationPanelMetrics.ActionIconSize),
                                )
                            },
                        )
                    }
                    val moveItem = remember(moveSpaceText) {
                        DropdownItem(
                            text = moveSpaceText,
                            icon = { modifier ->
                                Icon(
                                    imageVector = Icons.Outlined.Folder,
                                    contentDescription = null,
                                    modifier = modifier.size(ConversationPanelMetrics.ActionIconSize),
                                )
                            },
                        )
                    }
                    val exportItem = remember(exportText) {
                        DropdownItem(
                            text = exportText,
                            icon = { modifier ->
                                Icon(
                                    imageVector = Icons.Outlined.Download,
                                    contentDescription = null,
                                    modifier = modifier.size(ConversationPanelMetrics.ActionIconSize),
                                )
                            },
                        )
                    }
                    val deleteItem = remember(deleteText) {
                        DropdownItem(
                            text = deleteText,
                            icon = { modifier ->
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null,
                                    modifier = modifier.size(ConversationPanelMetrics.ActionIconSize),
                                    tint = MiuixTheme.colorScheme.error,
                                )
                            },
                        )
                    }
                    val deleteColors = DropdownDefaults.dropdownColors(
                        contentColor = MiuixTheme.colorScheme.error,
                        selectedContentColor = MiuixTheme.colorScheme.error,
                        selectedIndicatorColor = MiuixTheme.colorScheme.error,
                    )
                    ListPopupColumn {
                        DropdownImpl(
                            item = pinItem,
                            optionSize = 5,
                            isSelected = false,
                            index = 0,
                            onSelectedIndexChange = {
                                showActionMenu = false
                                onTogglePin()
                            },
                        )
                        DropdownImpl(
                            item = renameItem,
                            optionSize = 5,
                            isSelected = false,
                            index = 1,
                            onSelectedIndexChange = {
                                showActionMenu = false
                                onRename()
                            },
                        )
                        DropdownImpl(
                            item = moveItem,
                            optionSize = 5,
                            isSelected = false,
                            index = 2,
                            onSelectedIndexChange = {
                                showActionMenu = false
                                onMoveToSpace()
                            },
                        )
                        DropdownImpl(
                            item = exportItem,
                            optionSize = 5,
                            isSelected = false,
                            index = 3,
                            onSelectedIndexChange = {
                                showActionMenu = false
                                onExport()
                            },
                        )
                        DropdownImpl(
                            item = deleteItem,
                            optionSize = 5,
                            isSelected = false,
                            index = 4,
                            dropdownColors = deleteColors,
                            onSelectedIndexChange = {
                                showActionMenu = false
                                onDelete()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyConversations(isSearching: Boolean) {
    val isCoui = LocalUiStyle.current == AppearanceUiStyle.COUI
    val dark = if (isCoui) CouiTheme.isDark else MiuixTheme.colorScheme.surface.luminance() < 0.5f
    val primaryTextColor = if (isCoui) {
        CouiPageDefaults.themeColors(dark).summary
    } else {
        MiuixTheme.colorScheme.onSurfaceVariantSummary
    }
    val badgeBg = conversationPaneCardColor()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = ConversationPanelMetrics.PaneHorizontalPadding + 12.dp,
                vertical = ConversationPanelMetrics.EmptyVerticalPadding,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(badgeBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isSearching) ConversationPaneIcons.Search else ConversationPaneIcons.BrandEta,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = primaryTextColor.copy(alpha = 0.72f),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(
                if (isSearching) R.string.conversation_no_results else R.string.conversation_empty,
            ),
            color = primaryTextColor,
            style = MiuixTheme.textStyles.body2,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
        if (!isSearching) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.conversation_empty_hint),
                color = primaryTextColor.copy(alpha = 0.75f),
                style = MiuixTheme.textStyles.footnote1.copy(fontSize = 12.sp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private sealed interface ConversationDrawerEntry {
    data class SpaceHeader(
        val space: ConversationSpaceUi,
        val expanded: Boolean,
    ) : ConversationDrawerEntry

    data class Conversation(
        val conversation: ConversationSummaryUi,
        /** 项目工作区下的会话缩进一级，默认工作区保持与上游一致的顶格行。 */
        val nested: Boolean = false,
    ) : ConversationDrawerEntry
}

private fun buildDrawerEntries(
    conversations: List<ConversationSummaryUi>,
    spaces: List<ConversationSpaceUi>,
    expandedSpaceKeys: Set<String>,
    collapsedSpaceKeys: Set<String>,
    query: String = "",
): List<ConversationDrawerEntry> {
    // 没有会话也没有用户项目时交给空态提示；否则至少给底部「新建工作区」留位置。
    if (conversations.isEmpty() && spaces.isEmpty()) return emptyList()
    val normalizedQuery = query.trim()
    val searchActive = normalizedQuery.isNotEmpty()
    // 会话列表已由 AgentAppState 按标题、预览与消息内容过滤，这里不再二次过滤，
    // 否则只有消息内容命中的会话会被丢掉。组标题只决定「整组保留并强制展开」。
    fun ConversationSpaceUi.titleMatches(): Boolean =
        title.contains(normalizedQuery, ignoreCase = true)

    val entries = mutableListOf<ConversationDrawerEntry>()
    val defaultSpace = spaces.firstOrNull { it.isDefault }
    val defaultKeys = setOfNotNull(defaultSpace?.spaceKey)
    val projectKeys = spaces.filterNot { it.isDefault }.mapTo(mutableSetOf()) { it.spaceKey }

    fun appendSpace(
        space: ConversationSpaceUi,
        items: List<ConversationSummaryUi>,
        expanded: Boolean,
        nested: Boolean,
    ) {
        entries += ConversationDrawerEntry.SpaceHeader(space = space, expanded = expanded)
        if (expanded) items.forEach {
            entries += ConversationDrawerEntry.Conversation(it, nested = nested)
        }
    }

    // 未归属（space_key = null）的会话都归默认工作区，它恒定排在最前。
    if (defaultSpace == null) {
        // 兜底：库里内置工作区缺失（老版本或被清库）时，至少把会话按平铺列表显示出来，
        // 不让侧栏出现「有会话却提示还没有对话」。
        conversations.forEach { entries += ConversationDrawerEntry.Conversation(it) }
    } else {
        val items = conversations
            .filter { it.spaceKey == null || it.spaceKey in defaultKeys }
        val titleHit = searchActive && defaultSpace.titleMatches()
        val expanded = if (searchActive) true else defaultSpace.spaceKey !in collapsedSpaceKeys
        if (items.isNotEmpty() || !searchActive || titleHit) {
            appendSpace(defaultSpace, items, expanded, nested = false)
        }
    }

    val bySpace = conversations
        .filter { it.spaceKey != null && it.spaceKey in projectKeys }
        .groupBy { it.spaceKey.orEmpty() }
    for (space in spaces) {
        if (space.isDefault) continue
        val spaceTitleHit = searchActive && space.titleMatches()
        val items = bySpace[space.spaceKey].orEmpty()
        // 空工作区也要占位，否则「新建工作区」点完像没生效；搜索时只留命中的组。
        if (items.isEmpty() && searchActive && !spaceTitleHit) continue
        val expanded = if (searchActive) true else space.spaceKey in expandedSpaceKeys
        appendSpace(space, items, expanded, nested = true)
    }
    return entries
}

@Composable
private fun ConversationSpaceHeader(
    header: ConversationDrawerEntry.SpaceHeader,
    isFirstSection: Boolean,
    onToggle: () -> Unit,
    onNewConversation: () -> Unit,
    onCreateProject: () -> Unit,
    onOpenInTerminal: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    var showSpaceMenu by remember { mutableStateOf(false) }
    val spaceMenuAnchor = rememberCouiMenuAnchor()
    val headerInteraction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // 外距与段落间距留在点击区域之外：涟漪只覆盖组头本体，和下面的会话行同宽同圆角。
            .padding(
                start = ConversationPanelMetrics.PaneHorizontalPadding,
                end = ConversationPanelMetrics.PaneHorizontalPadding,
                top = if (isFirstSection) {
                    ConversationPanelMetrics.FirstSectionTopPadding
                } else {
                    ConversationPanelMetrics.SectionTopPadding
                },
                bottom = ConversationPanelMetrics.SectionBottomPadding,
            )
            .clip(RoundedCornerShape(ConversationPanelMetrics.RowCornerRadius))
            .then(
                conversationSpaceClickable(
                    interactionSource = headerInteraction,
                    onClick = onToggle,
                ),
            )
            .padding(
                horizontal = ConversationPanelMetrics.RowHorizontalPadding,
                vertical = ConversationPanelMetrics.RowVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ConversationSpaceGlyph(
            imageVector = if (header.expanded) ConversationPaneIcons.FolderOpen else ConversationPaneIcons.Folder,
            contentDescription = null,
            modifier = Modifier.size(ConversationSpaceMetrics.HeaderIconSize),
        )
        Spacer(Modifier.width(ConversationSpaceMetrics.HeaderIconGap))
        ConversationSpaceLabel(
            text = header.space.title,
            // 组标题对读屏是「标题」，与上游分段的 heading 语义一致。
            modifier = Modifier
                .weight(1f)
                .semantics { heading() },
        )
        ConversationSpaceAction(
            icon = ConversationPaneIcons.MoreVert,
            contentDescription = stringResource(R.string.project_manage_title),
            onClick = { spaceMenuAnchor.open { showSpaceMenu = true } },
            modifier = Modifier.couiMenuAnchor(spaceMenuAnchor),
        )
        ProjectSpaceMenu(
            expanded = showSpaceMenu,
            onDismissRequest = { showSpaceMenu = false },
            anchor = spaceMenuAnchor,
            canManage = !header.space.isDefault,
            onCreateProject = {
                showSpaceMenu = false
                onCreateProject()
            },
            onOpenInTerminal = {
                showSpaceMenu = false
                onOpenInTerminal()
            },
            onRename = {
                showSpaceMenu = false
                onRename()
            },
            onDelete = {
                showSpaceMenu = false
                onDelete()
            },
        )
        ConversationSpaceAction(
            icon = ChatActionIcons.NewChat,
            contentDescription = stringResource(R.string.project_new_conversation),
            onClick = onNewConversation,
        )
    }
}

@Composable
private fun ProjectSpaceMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    anchor: com.itos.coui.menu.CouiMenuAnchor,
    canManage: Boolean,
    onCreateProject: () -> Unit,
    onOpenInTerminal: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    val actions = remember(canManage) { conversationSpaceMenuActions(canManage) }
    val terminalText = stringResource(R.string.project_open_in_terminal)
    val createProjectText = stringResource(R.string.project_create)
    val renameText = stringResource(R.string.project_rename)
    val deleteText = stringResource(R.string.project_delete)

    fun textOf(action: ConversationSpaceMenuAction): String = when (action) {
        ConversationSpaceMenuAction.OpenInTerminal -> terminalText
        ConversationSpaceMenuAction.CreateProject -> createProjectText
        ConversationSpaceMenuAction.Rename -> renameText
        ConversationSpaceMenuAction.Delete -> deleteText
    }

    fun dispatch(action: ConversationSpaceMenuAction) = dispatchConversationSpaceMenuAction(
        action = action,
        onOpenInTerminal = onOpenInTerminal,
        onCreateProject = onCreateProject,
        onRename = onRename,
        onDelete = onDelete,
    )

    when (LocalUiStyle.current) {
        AppearanceUiStyle.COUI -> {
            val destructiveColor = CouiDialogDefaults.themeColors().destructive
            val items = remember(
                actions,
                terminalText,
                createProjectText,
                renameText,
                deleteText,
                destructiveColor,
            ) {
                actions.mapIndexed { index, action ->
                    when (action) {
                        ConversationSpaceMenuAction.OpenInTerminal -> CouiMenuItem(
                            value = index,
                            text = terminalText,
                            icon = ConversationPaneIcons.Terminal,
                        )
                        ConversationSpaceMenuAction.CreateProject -> CouiMenuItem(
                            value = index,
                            text = createProjectText,
                            icon = ConversationPaneIcons.Folder,
                        )
                        ConversationSpaceMenuAction.Rename -> CouiMenuItem(
                            value = index,
                            text = renameText,
                            icon = ConversationPaneIcons.Edit,
                        )
                        ConversationSpaceMenuAction.Delete -> CouiMenuItem(
                            value = index,
                            text = deleteText,
                            icon = ConversationPaneIcons.Trash,
                            iconTint = destructiveColor,
                            dividerBefore = true,
                        )
                    }
                }
            }
            Menu(
                expanded = expanded,
                onDismissRequest = onDismissRequest,
                items = items,
                onItemClick = { item -> actions.getOrNull(item.value)?.let(::dispatch) },
                anchor = anchor,
            )
        }
        AppearanceUiStyle.MIUIX -> {
            val optionSize = actions.size
            WindowListPopup(
                show = expanded,
                popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                alignment = PopupPositionProvider.Align.BottomEnd,
                onDismissRequest = onDismissRequest,
            ) {
                ListPopupColumn {
                    actions.forEachIndexed { index, action ->
                        if (action == ConversationSpaceMenuAction.Delete) {
                            val deleteColors = DropdownDefaults.dropdownColors(
                                contentColor = MiuixTheme.colorScheme.error,
                                selectedContentColor = MiuixTheme.colorScheme.error,
                                selectedIndicatorColor = MiuixTheme.colorScheme.error,
                            )
                            DropdownImpl(
                                item = DropdownItem(text = textOf(action)),
                                optionSize = optionSize,
                                isSelected = false,
                                index = index,
                                dropdownColors = deleteColors,
                                onSelectedIndexChange = { dispatch(action) },
                            )
                        } else {
                            DropdownImpl(
                                item = DropdownItem(text = textOf(action)),
                                optionSize = optionSize,
                                isSelected = false,
                                index = index,
                                onSelectedIndexChange = { dispatch(action) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 组头菜单的固定项：默认工作区不可重命名/删除，所有工作区都能开终端与新建项目。 */
internal enum class ConversationSpaceMenuAction {
    OpenInTerminal,
    CreateProject,
    Rename,
    Delete,
}

internal fun conversationSpaceMenuActions(canManage: Boolean): List<ConversationSpaceMenuAction> = buildList {
    add(ConversationSpaceMenuAction.OpenInTerminal)
    add(ConversationSpaceMenuAction.CreateProject)
    if (canManage) {
        add(ConversationSpaceMenuAction.Rename)
        add(ConversationSpaceMenuAction.Delete)
    }
}

/** COUI 与 Miuix 两套菜单共用同一份派发逻辑，避免两边索引各写一套 `when`。 */
internal fun dispatchConversationSpaceMenuAction(
    action: ConversationSpaceMenuAction,
    onOpenInTerminal: () -> Unit,
    onCreateProject: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    when (action) {
        ConversationSpaceMenuAction.OpenInTerminal -> onOpenInTerminal()
        ConversationSpaceMenuAction.CreateProject -> onCreateProject()
        ConversationSpaceMenuAction.Rename -> onRename()
        ConversationSpaceMenuAction.Delete -> onDelete()
    }
}

private val ConversationNestedRowIndent = ConversationSpaceMetrics.NestedRowIndent

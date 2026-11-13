package io.github.mangi.eta.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SportsBar
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.itos.coui.dialog.CouiDialogDefaults
import com.itos.coui.menu.CouiMenuItem
import com.itos.coui.menu.Menu
import com.itos.coui.menu.couiMenuAnchor
import com.itos.coui.menu.rememberCouiMenuAnchor
import com.itos.coui.shape.CouiShape
import com.itos.coui.theme.CouiTheme
import io.github.mangi.eta.R
import io.github.mangi.eta.data.model.AppearanceUiStyle
import io.github.mangi.eta.ui.app.LocalUiStyle
import io.github.mangi.eta.ui.components.coui.CouiSearchBarBox
import io.github.mangi.eta.ui.model.ConversationPaneUiState
import io.github.mangi.eta.ui.model.ConversationSpaceUi
import io.github.mangi.eta.ui.model.ConversationSummaryUi
import top.yukonga.miuix.kmp.basic.DropdownDefaults
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowListPopup

/**
 * 会话侧边栏尺寸标尺。
 *
 * 外层水平边距（12.dp）+ 行内水平边距（16.dp）= 28.dp，与 Miuix 原生 [SmallTitle] 的默认内边距
 * 及 [InputField] 内部搜索图标起点完全对齐，保证搜索框、分组标题与对话标题处于同一条垂直基准线。
 */
private object ConversationPanelMetrics {
    val PaneHorizontalPadding = 12.dp
    val TopInset = 8.dp
    val AfterActionBar = 8.dp
    val BottomInset = 4.dp
    val ActionIconSize = 20.dp
    val FirstSectionTopPadding = 8.dp
    val SectionTopPadding = 16.dp
    val SectionBottomPadding = 6.dp
    val RowMinHeight = 44.dp
    val RowGap = 2.dp
    val RowCornerRadius = 14.dp
    val RowHorizontalPadding = 16.dp
    val RowVerticalPadding = 11.dp
    val ActiveDotSize = 6.dp
    val ActiveDotGap = 10.dp
    val EmptyVerticalPadding = 28.dp
    val DockVerticalPadding = 4.dp
    val DockEntryCornerRadius = 12.dp
    val DockEntryIconSize = 20.dp
}

/**
 * 侧栏与外层推移容器共用的背景色。
 *
 * 深色下聊天区保持 surface（纯黑/近黑），侧栏沿用同一颜色会在展开后与聊天区融为一体；
 * 抬高一档到 surfaceContainer，保证两侧始终可分。浅色维持 surface，避免改变现有观感。
 */
@Composable
internal fun conversationPaneContainerColor(): Color {
    if (LocalUiStyle.current == AppearanceUiStyle.COUI) {
        return if (CouiTheme.isDark) Color(0xFF18191C) else Color(0xFFF3F4F6)
    }
    val colors = MiuixTheme.colorScheme
    return if (colors.background.luminance() > 0.5f) colors.surface else colors.surfaceContainer
}

@Composable
internal fun ConversationPanePanel(
    state: ConversationPaneUiState,
    width: androidx.compose.ui.unit.Dp,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onSearchChange: (String) -> Unit,
    onConversationSelected: (String) -> Unit,
    onConversationTogglePin: (ConversationSummaryUi) -> Unit,
    onConversationRename: (ConversationSummaryUi) -> Unit,
    onConversationExport: (ConversationSummaryUi) -> Unit,
    onConversationDelete: (ConversationSummaryUi) -> Unit,
    onConversationMoveToSpace: (ConversationSummaryUi) -> Unit,
    /** 在某个项目里新建对话（默认工作区传 isDefault 的分组）。 */
    onNewConversationInSpace: (ConversationSpaceUi) -> Unit,
    onOpenProjectInTerminal: (ConversationSpaceUi) -> Unit,
    onRenameProjectSpace: (ConversationSpaceUi) -> Unit,
    onDeleteProjectSpace: (ConversationSpaceUi) -> Unit,
    onCreateProject: () -> Unit,
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
    // 项目分组默认折叠：会话多时列表首先是一屏分组头，展开才进入组内。
    var expandedSpaceKeys by remember { mutableStateOf(setOf<String>()) }
    // 默认工作区反过来：默认展开，点一下收起，所以单独记「被收起的键」。
    var collapsedSpaceKeys by remember { mutableStateOf(setOf<String>()) }
    val entries = remember(state.conversations, state.spaces, expandedSpaceKeys, collapsedSpaceKeys, query) {
        buildDrawerEntries(
            conversations = state.conversations,
            spaces = state.spaces,
            expandedSpaceKeys = expandedSpaceKeys,
            collapsedSpaceKeys = collapsedSpaceKeys,
            query = query,
        )
    }

    Surface(
        modifier = modifier
            .width(width)
            .fillMaxHeight(),
        color = conversationPaneContainerColor(),
        contentColor = MiuixTheme.colorScheme.onSurface,
    ) {
        // 搜索与工具条占据独立布局空间，列表只在中间视口内滚动和回弹。
        Column(modifier = Modifier.fillMaxSize()) {
            PaneFixedRegion {
                Column(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                        )
                        .padding(horizontal = ConversationPanelMetrics.PaneHorizontalPadding),
                ) {
                    Spacer(Modifier.height(ConversationPanelMetrics.TopInset))
                    PaneActionBar(query = state.searchQuery, onSearchChange = onSearchChange)
                    Spacer(Modifier.height(ConversationPanelMetrics.AfterActionBar))
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clipToBounds()
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                    )
                    .padding(horizontal = ConversationPanelMetrics.PaneHorizontalPadding)
                    .scrollEndHaptic()
                    .overScrollVertical(),
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(ConversationPanelMetrics.RowGap),
                overscrollEffect = null,
            ) {
                if (entries.isEmpty()) {
                    item {
                        EmptyConversations(isSearching = query.isNotBlank())
                    }
                } else {
                    entries.forEachIndexed { entryIndex, entry ->
                        when (entry) {
                            is ConversationDrawerEntry.SpaceHeader -> item(key = "space-${entry.space.spaceKey}") {
                                ConversationSpaceHeader(
                                    header = entry,
                                    isFirstSection = entryIndex == 0,
                                    onToggle = {
                                        if (entry.space.isDefault) {
                                            collapsedSpaceKeys = if (entry.expanded) {
                                                collapsedSpaceKeys + entry.space.spaceKey
                                            } else {
                                                collapsedSpaceKeys - entry.space.spaceKey
                                            }
                                        } else {
                                            expandedSpaceKeys = if (entry.expanded) {
                                                expandedSpaceKeys - entry.space.spaceKey
                                            } else {
                                                expandedSpaceKeys + entry.space.spaceKey
                                            }
                                        }
                                    },
                                    onNewConversation = { onNewConversationInSpace(entry.space) },
                                    onOpenInTerminal = { onOpenProjectInTerminal(entry.space) },
                                    onRename = { onRenameProjectSpace(entry.space) },
                                    onDelete = { onDeleteProjectSpace(entry.space) },
                                )
                            }
                            is ConversationDrawerEntry.SectionHeader ->
                                item(key = "section-${entry.spaceKey}-${entry.group.section}") {
                                ConversationSectionHeader(
                                    group = entry.group,
                                    isFirstSection = entryIndex == 0,
                                    nested = entry.nested,
                                )
                                }
                            is ConversationDrawerEntry.Conversation -> item(key = entry.conversation.id) {
                                val conversation = entry.conversation
                                ConversationTextRow(
                                    conversation = conversation,
                                    selected = conversation.id == state.selectedConversationId,
                                    onClick = { onConversationSelected(conversation.id) },
                                    onTogglePin = { onConversationTogglePin(conversation) },
                                    onRename = { onConversationRename(conversation) },
                                    onExport = { onConversationExport(conversation) },
                                    onDelete = { onConversationDelete(conversation) },
                                    onMoveToSpace = { onConversationMoveToSpace(conversation) },
                                    startPadding = if (entry.nested) ConversationNestedRowIndent else 0.dp,
                                )
                            }
                        }
                    }
                }
                item(key = "new-project") {
                    NewProjectRow(onClick = onCreateProject)
                }
            }
            PaneFixedRegion {
                Column(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
                        )
                        .padding(horizontal = ConversationPanelMetrics.PaneHorizontalPadding),
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = ConversationPanelMetrics.RowHorizontalPadding),
                        thickness = 0.5.dp,
                    )
                    Spacer(Modifier.height(ConversationPanelMetrics.DockVerticalPadding))
                    PaneDock(
                        onOpenSettings = onOpenSettings,
                        onOpenModelProviders = onOpenModelProviders,
                        onOpenTools = onOpenTools,
                        onOpenSkills = onOpenSkills,
                        onOpenCharacters = onOpenCharacters,
                        onOpenPermissions = onOpenPermissions,
                    )
                    Spacer(Modifier.height(ConversationPanelMetrics.BottomInset))
                }
            }
        }
    }
}

@Composable
private fun PaneFixedRegion(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(conversationPaneContainerColor()),
    ) {
        content()
    }
}

@Composable
private fun PaneActionBar(
    query: String,
    onSearchChange: (String) -> Unit,
) {
    when (LocalUiStyle.current) {
        AppearanceUiStyle.COUI -> {
            CouiSearchBarBox(
                query = query,
                onQueryChange = onSearchChange,
                placeholder = stringResource(R.string.conversation_search_hint),
                backgroundColor = conversationPaneContainerColor(),
            )
        }
        AppearanceUiStyle.MIUIX -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SearchBar(
                    modifier = Modifier.weight(1f),
                    insideMargin = DpSize.Zero,
                    expanded = false,
                    onExpandedChange = {},
                    inputField = {
                        InputField(
                            query = query,
                            onQueryChange = onSearchChange,
                            onSearch = onSearchChange,
                            expanded = false,
                            onExpandedChange = {},
                            label = stringResource(R.string.conversation_search_hint),
                            color = MiuixTheme.colorScheme.surfaceContainerHighest,
                        )
                    },
                    content = {},
                )
            }
        }
    }
}

@Composable
private fun ConversationSectionHeader(
    group: ConversationDrawerGroup,
    isFirstSection: Boolean,
    nested: Boolean = false,
) {
    SmallTitle(
        text = group.localizedLabel(),
        insideMargin = PaddingValues(
            start = ConversationPanelMetrics.RowHorizontalPadding +
                if (nested) NestedSectionIndent else 0.dp,
            top = if (isFirstSection) {
                ConversationPanelMetrics.FirstSectionTopPadding
            } else {
                ConversationPanelMetrics.SectionTopPadding
            },
            end = ConversationPanelMetrics.RowHorizontalPadding,
            bottom = ConversationPanelMetrics.SectionBottomPadding,
        ),
    )
}

@Composable
private fun ConversationSpaceHeader(
    header: ConversationDrawerEntry.SpaceHeader,
    isFirstSection: Boolean,
    onToggle: () -> Unit,
    onNewConversation: () -> Unit,
    onOpenInTerminal: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onToggle)
            .padding(
                start = ConversationPanelMetrics.RowHorizontalPadding,
                end = ConversationPanelMetrics.RowHorizontalPadding,
                top = if (isFirstSection) {
                    ConversationPanelMetrics.FirstSectionTopPadding
                } else {
                    ConversationPanelMetrics.SectionTopPadding
                },
                bottom = ConversationPanelMetrics.SectionBottomPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (header.expanded) Icons.Filled.FolderOpen else Icons.Outlined.Folder,
            contentDescription = null,
            tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = header.space.title,
            style = MiuixTheme.textStyles.footnote1,
            fontWeight = FontWeight.SemiBold,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        HeaderIconButton(
            icon = Icons.Outlined.Add,
            contentDescription = stringResource(R.string.project_new_conversation),
            onClick = onNewConversation,
        )
        if (!header.space.isDefault) {
            var showProjectMenu by remember { mutableStateOf(false) }
            val projectMenuAnchor = rememberCouiMenuAnchor()
            HeaderIconButton(
                icon = Icons.Outlined.MoreVert,
                contentDescription = stringResource(R.string.project_manage_title),
                onClick = { projectMenuAnchor.open { showProjectMenu = true } },
                modifier = Modifier.couiMenuAnchor(projectMenuAnchor),
            )
            ProjectSpaceMenu(
                expanded = showProjectMenu,
                onDismissRequest = { showProjectMenu = false },
                anchor = projectMenuAnchor,
                onOpenInTerminal = {
                    showProjectMenu = false
                    onOpenInTerminal()
                },
                onRename = {
                    showProjectMenu = false
                    onRename()
                },
                onDelete = {
                    showProjectMenu = false
                    onDelete()
                },
            )
        }
        Text(
            text = header.count.toString(),
            style = MiuixTheme.textStyles.footnote2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ConversationTextRow(
    conversation: ConversationSummaryUi,
    selected: Boolean,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onRename: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit,
    onMoveToSpace: () -> Unit,
    startPadding: Dp = 0.dp,
) {
    var showActionMenu by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val anchor = rememberCouiMenuAnchor()
    val isCoui = LocalUiStyle.current == AppearanceUiStyle.COUI
    val couiDark = CouiTheme.isDark
    val couiRowShape = remember { CouiShape(14.dp, 2f) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = startPadding)
                .heightIn(min = ConversationPanelMetrics.RowMinHeight)
                .couiMenuAnchor(anchor)
                .then(
                    if (isCoui) {
                        Modifier
                            .clip(couiRowShape)
                            .background(
                                if (selected) {
                                    if (couiDark) Color(0xFF26292E) else Color.White
                                } else {
                                    Color.Transparent
                                },
                                couiRowShape,
                            )
                    } else {
                        Modifier.squircleSurface(
                            color = if (selected) {
                                MiuixTheme.colorScheme.tertiaryContainer
                            } else {
                                Color.Transparent
                            },
                            cornerRadius = ConversationPanelMetrics.RowCornerRadius,
                        )
                    },
                )
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        anchor.open { showActionMenu = true }
                    },
                )
                .padding(
                    horizontal = ConversationPanelMetrics.RowHorizontalPadding,
                    vertical = ConversationPanelMetrics.RowVerticalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            conversation.characterName?.let { name ->
                CharacterAvatar(
                    name = name,
                    avatarPath = conversation.characterAvatarPath,
                    size = 32.dp,
                    shape = CircleShape,
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                val title = conversation.title.ifBlank { conversation.preview }
                Text(
                    text = title,
                    color = if (selected) {
                        MiuixTheme.colorScheme.onTertiaryContainer
                    } else {
                        MiuixTheme.colorScheme.onSurface
                    },
                    style = MiuixTheme.textStyles.body1,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // 标题与角色名相同（如未改名的角色会话）时不再重复第二行。
                conversation.characterName?.takeIf { it != title }?.let { name ->
                    Text(
                        text = name,
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                conversation.runStatus?.let { status ->
                    Text(
                        text = status,
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                        .background(MiuixTheme.colorScheme.primary),
                )
            }
        }

        val pinText = stringResource(
            if (conversation.isPinned) {
                R.string.action_unpin_conversation
            } else {
                R.string.action_pin_conversation
            },
        )
        val renameText = stringResource(R.string.action_rename)
        val moveSpaceText = stringResource(R.string.action_move_to_space)
        val exportText = stringResource(R.string.action_export)
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
                            4 -> onDelete()
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
                    val moveSpaceItem = remember(moveSpaceText) {
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
                            item = moveSpaceItem,
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
    Text(
        text = stringResource(
            if (isSearching) R.string.conversation_no_results else R.string.conversation_empty,
        ),
        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        style = MiuixTheme.textStyles.body2,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(
            horizontal = ConversationPanelMetrics.RowHorizontalPadding,
            vertical = ConversationPanelMetrics.EmptyVerticalPadding,
        ),
    )
}

@Composable
private fun PaneDock(
    onOpenSettings: () -> Unit,
    onOpenModelProviders: () -> Unit,
    onOpenTools: () -> Unit,
    onOpenSkills: () -> Unit,
    onOpenCharacters: () -> Unit,
    onOpenPermissions: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DockEntry(
            icon = Icons.Outlined.Settings,
            label = "设置",
            onClick = onOpenSettings,
            modifier = Modifier.weight(1f),
        )
        DockEntry(
            icon = Icons.Outlined.Cloud,
            label = "模型",
            onClick = onOpenModelProviders,
            modifier = Modifier.weight(1f),
        )
        DockEntry(
            icon = Icons.Outlined.Widgets,
            label = "工具",
            onClick = onOpenTools,
            modifier = Modifier.weight(1f),
        )
        DockEntry(
            icon = Icons.Outlined.AutoStories,
            label = "Skills",
            onClick = onOpenSkills,
            modifier = Modifier.weight(1f),
        )
        DockEntry(
            icon = Icons.Outlined.Shield,
            label = "权限",
            onClick = onOpenPermissions,
            modifier = Modifier.weight(1f),
        )
        DockEntry(
            icon = Icons.Outlined.SportsBar,
            label = "角色",
            onClick = onOpenCharacters,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DockEntry(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .heightIn(min = 46.dp)
            .squircleSurface(
                color = Color.Transparent,
                cornerRadius = ConversationPanelMetrics.DockEntryCornerRadius,
            )
            .clickable(onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(ConversationPanelMetrics.DockEntryIconSize),
            tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
        )
    }
}

/** 侧栏一条「可渲染行」：项目头、时间分组头、会话行。展开状态只影响条目列表本身。 */
private sealed interface ConversationDrawerEntry {
    data class SpaceHeader(
        val space: ConversationSpaceUi,
        val count: Int,
        val expanded: Boolean,
    ) : ConversationDrawerEntry

    data class SectionHeader(
        val group: ConversationDrawerGroup,
        /** 组内嵌的时间分组，渲染时左缩进一级。 */
        val nested: Boolean = false,
        /** 所属项目；同一 section 在多个项目里各有一份，item key 要靠它区分。 */
        val spaceKey: String? = null,
    ) : ConversationDrawerEntry

    data class Conversation(
        val conversation: ConversationSummaryUi,
        /** 组内的会话行，渲染时左缩进，形成树形层级。 */
        val nested: Boolean = false,
    ) : ConversationDrawerEntry
}

/**
 * 项目分组在前（默认折叠，只在展开时把它们名下的会话铺开），
 * 未归属/默认工作区的会话随后仍走原来的置顶·今天·日期分组，历史观感不变。
 */
private fun buildDrawerEntries(
    conversations: List<ConversationSummaryUi>,
    spaces: List<ConversationSpaceUi>,
    expandedSpaceKeys: Set<String>,
    collapsedSpaceKeys: Set<String>,
    query: String = "",
): List<ConversationDrawerEntry> {
    // 一个会话都没有、也没有用户项目时，交给空态提示；否则至少要给「新建项目」留位置。
    if (conversations.isEmpty() && spaces.none { !it.isDefault }) return emptyList()
    val normalizedQuery = query.trim()
    val searchActive = normalizedQuery.isNotEmpty()
    fun ConversationSummaryUi.matchesSearch(): Boolean =
        title.contains(normalizedQuery, ignoreCase = true) ||
            preview.contains(normalizedQuery, ignoreCase = true)
    val entries = mutableListOf<ConversationDrawerEntry>()
    val defaultSpace = spaces.firstOrNull { it.isDefault }
    val defaultKeys = setOfNotNull(defaultSpace?.spaceKey)
    val projectKeys = spaces.filterNot { it.isDefault }.mapTo(mutableSetOf()) { it.spaceKey }

    // 默认工作区永远排最前：未归属（space_key = null）的会话都归它，组内保留置顶·今天·日期分组。
    val defaultItems = conversations
        .filter { it.spaceKey == null || it.spaceKey in defaultKeys }
        .filter { !searchActive || it.matchesSearch() }
    if (defaultSpace != null) {
        val defaultExpanded = if (searchActive) true else defaultSpace.spaceKey !in collapsedSpaceKeys
        entries += ConversationDrawerEntry.SpaceHeader(
            space = defaultSpace,
            count = defaultItems.size,
            expanded = defaultExpanded,
        )
        if (defaultExpanded) {
            defaultItems.groupForDrawer().forEach { group ->
                entries += ConversationDrawerEntry.SectionHeader(
                    group = group,
                    nested = true,
                    spaceKey = defaultSpace.spaceKey,
                )
                group.items.forEach {
                    entries += ConversationDrawerEntry.Conversation(it, nested = true)
                }
            }
        }
    }

    val bySpace = conversations
        .filter { it.spaceKey != null && it.spaceKey in projectKeys }
        .groupBy { it.spaceKey.orEmpty() }
    for (space in spaces) {
        if (space.isDefault) continue
        val spaceTitleHit = searchActive && space.title.contains(normalizedQuery, ignoreCase = true)
        val items = bySpace[space.spaceKey].orEmpty()
            .let { if (!searchActive || spaceTitleHit) it else it.filter { c -> c.matchesSearch() } }
        // 空项目也要占位，否则「新建项目」点完像没生效；搜索时只留下命中的组。
        if (items.isEmpty() && searchActive && !spaceTitleHit) continue
        // 搜索命中的项目组强制展开，但不改写用户手动的 expandedSpaceKeys。
        val expanded = if (searchActive) true else space.spaceKey in expandedSpaceKeys
        entries += ConversationDrawerEntry.SpaceHeader(space = space, count = items.size, expanded = expanded)
        if (!expanded) continue
        // 组内沿用置顶·今天·日期分组，让项目里的会话也呈树形，而不是一条平铺列表。
        items.groupForDrawer().forEach { group ->
            entries += ConversationDrawerEntry.SectionHeader(
                group = group,
                nested = true,
                spaceKey = space.spaceKey,
            )
            group.items.forEach {
                entries += ConversationDrawerEntry.Conversation(it, nested = true)
            }
        }
    }
    return entries
}

private data class ConversationDrawerGroup(
    val section: ConversationDrawerSection,
    val items: List<ConversationSummaryUi>,
)

private sealed interface ConversationDrawerSection {
    data object Pinned : ConversationDrawerSection
    data object Today : ConversationDrawerSection
    data class Dated(val label: String) : ConversationDrawerSection
}

@Composable
private fun ConversationDrawerGroup.localizedLabel(): String = when (val value = section) {
    ConversationDrawerSection.Pinned -> stringResource(R.string.conversation_section_pinned)
    ConversationDrawerSection.Today -> stringResource(R.string.conversation_section_today)
    is ConversationDrawerSection.Dated -> value.label
}

private fun List<ConversationSummaryUi>.groupForDrawer(): List<ConversationDrawerGroup> {
    if (isEmpty()) return emptyList()
    val groups = mutableListOf<ConversationDrawerGroup>()
    for (conversation in this) {
        val section = conversation.drawerSection()
        val last = groups.lastOrNull()
        if (last?.section == section) {
            groups[groups.lastIndex] = last.copy(items = last.items + conversation)
        } else {
            groups += ConversationDrawerGroup(section = section, items = listOf(conversation))
        }
    }
    return groups
}

private fun ConversationSummaryUi.drawerSection(): ConversationDrawerSection = when {
    isPinned -> ConversationDrawerSection.Pinned
    isActiveRun || isUpdatedToday(updatedAtMillis) -> ConversationDrawerSection.Today
    else -> ConversationDrawerSection.Dated(timeLabel)
}

private fun isUpdatedToday(timestampMillis: Long): Boolean {
    if (timestampMillis <= 0L) return true
    val now = java.util.Calendar.getInstance()
    val target = java.util.Calendar.getInstance().apply { timeInMillis = timestampMillis }
    return now.get(java.util.Calendar.ERA) == target.get(java.util.Calendar.ERA) &&
        now.get(java.util.Calendar.YEAR) == target.get(java.util.Calendar.YEAR) &&
        now.get(java.util.Calendar.DAY_OF_YEAR) == target.get(java.util.Calendar.DAY_OF_YEAR)
}

/** 默认工作区组内的时间分组缩进量。 */
private val NestedSectionIndent = 12.dp

/** 组内会话行的左缩进：比时间分组头再深一级，读起来像树。 */
private val ConversationNestedRowIndent = 22.dp

@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.size(16.dp),
        )
    }
}

/** 项目组头的锚定菜单：在终端打开 / 重命名 / 删除。 */
@Composable
private fun ProjectSpaceMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    anchor: com.itos.coui.menu.CouiMenuAnchor,
    onOpenInTerminal: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    val terminalText = stringResource(R.string.project_open_in_terminal)
    val renameText = stringResource(R.string.project_rename)
    val deleteText = stringResource(R.string.project_delete)
    when (LocalUiStyle.current) {
        AppearanceUiStyle.COUI -> {
            val destructiveColor = CouiDialogDefaults.themeColors().destructive
            val items = remember(terminalText, renameText, deleteText, destructiveColor) {
                listOf(
                    CouiMenuItem(value = 0, text = terminalText, icon = Icons.Outlined.Terminal),
                    CouiMenuItem(value = 1, text = renameText, icon = Icons.Outlined.Edit),
                    CouiMenuItem(
                        value = 2,
                        text = deleteText,
                        icon = Icons.Outlined.Delete,
                        iconTint = destructiveColor,
                        dividerBefore = true,
                    ),
                )
            }
            Menu(
                expanded = expanded,
                onDismissRequest = onDismissRequest,
                items = items,
                onItemClick = { item ->
                    when (item.value) {
                        0 -> onOpenInTerminal()
                        1 -> onRename()
                        else -> onDelete()
                    }
                },
                anchor = anchor,
            )
        }
        AppearanceUiStyle.MIUIX -> {
            WindowListPopup(
                show = expanded,
                popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                alignment = PopupPositionProvider.Align.BottomEnd,
                onDismissRequest = onDismissRequest,
            ) {
                ListPopupColumn {
                    DropdownImpl(
                        item = DropdownItem(text = terminalText),
                        optionSize = 3,
                        isSelected = false,
                        index = 0,
                        onSelectedIndexChange = { onOpenInTerminal() },
                    )
                    DropdownImpl(
                        item = DropdownItem(text = renameText),
                        optionSize = 3,
                        isSelected = false,
                        index = 1,
                        onSelectedIndexChange = { onRename() },
                    )
                    val deleteColors = DropdownDefaults.dropdownColors(
                        contentColor = MiuixTheme.colorScheme.error,
                        selectedContentColor = MiuixTheme.colorScheme.error,
                        selectedIndicatorColor = MiuixTheme.colorScheme.error,
                    )
                    DropdownImpl(
                        item = DropdownItem(text = deleteText),
                        optionSize = 3,
                        isSelected = false,
                        index = 2,
                        dropdownColors = deleteColors,
                        onSelectedIndexChange = { onDelete() },
                    )
                }
            }
        }
    }
}

/** 列表底部的「新建项目」入口：不依赖任何会话，先建项目再往里放对话。 */
@Composable
private fun NewProjectRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ConversationPanelMetrics.PaneHorizontalPadding)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = ConversationPanelMetrics.RowHorizontalPadding, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.project_create),
            style = MiuixTheme.textStyles.footnote1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

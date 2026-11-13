package io.github.mangi.eta.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itos.coui.effect.couiButtonFeedback
import com.itos.coui.layout.CouiPageDefaults
import com.itos.coui.text.CouiTextKind
import com.itos.coui.text.Text as CouiText
import com.itos.coui.theme.CouiTheme
import io.github.mangi.eta.data.model.AppearanceUiStyle
import io.github.mangi.eta.ui.app.LocalUiStyle
import io.github.mangi.eta.ui.components.coui.CouiIcon
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 工作区（ProjectSpace）侧栏样式令牌。
 *
 * 工作区的**布局事实**（行高、缩进、折叠语义、菜单位次）在两套风格下完全一致，只有
 * 色彩、字体与按压反馈属于风格差异。所以这里只放风格相关的令牌，布局常量直接写在本文件，
 * 业务组件不得再出现 `MiuixTheme.xxx` 或 `CouiPageDefaults.xxx` 的直接引用 —— 否则会出现
 * 「COUI 风格下工作区仍用 Miuix 配色/字号」这类静默走样。
 */
internal data class ConversationSpaceTokens(
    /** 组头标题色。COUI 取页面标题色，Miuix 取 onSurfaceVariantSummary。 */
    val titleColor: Color,
    /** 组头文件夹图标色。 */
    val iconTint: Color,
    /** 组头 ⋯/＋ 动作图标色。 */
    val actionTint: Color,
    /** 选择器里选中项的勾选色。 */
    val checkedTint: Color,
    /** 组头标题字重。COUI preference 标题自带字重，Miuix footnote1 需要显式加粗。 */
    val titleWeight: FontWeight,
    /**
     * 组头与选择器行的圆角。
     *
     * COUI 目前没有公开的列表圆角规范（2026-11 核对），12dp 是延续本项目 COUI 页面既有
     * 圆角得出的**项目约定值**，不是规范值；Miuix 侧按 squircle 观感取 10dp。两风格圆角
     * 略有差异是刻意保留的风格区分，切换时允许可见差；将来 COUI 若发布列表圆角规范，
     * 只需改这一处取值，调用点无需改动。
     */
    val headerCornerRadius: Dp,
)

/** 两套风格共用的工作区布局事实。 */
internal object ConversationSpaceMetrics {
    val HeaderIconSize = 16.dp
    val HeaderIconGap = 6.dp
    val ActionButtonSize = 28.dp
    val ActionIconSize = 16.dp
    val NestedRowIndent = 22.dp
    val PickerRowGap = 2.dp
    val PickerRowMinHeight = 40.dp
    val PickerRowHorizontalPadding = 12.dp
    val PickerCheckSize = 16.dp
}

@Composable
internal fun rememberConversationSpaceTokens(): ConversationSpaceTokens {
    if (LocalUiStyle.current == AppearanceUiStyle.COUI) {
        val dark = CouiTheme.isDark
        val colors = CouiPageDefaults.themeColors(dark)
        val accent = CouiTheme.colorScheme.accent
        return remember(dark, accent) {
            ConversationSpaceTokens(
                titleColor = colors.title,
                iconTint = colors.summary,
                actionTint = colors.summary,
                checkedTint = accent,
                titleWeight = FontWeight.Medium,
                headerCornerRadius = 12.dp,
            )
        }
    }
    val colorScheme = MiuixTheme.colorScheme
    return remember(colorScheme) {
        ConversationSpaceTokens(
            titleColor = colorScheme.onSurfaceVariantSummary,
            iconTint = colorScheme.onSurfaceVariantSummary,
            actionTint = colorScheme.onSurfaceVariantSummary,
            checkedTint = colorScheme.primary,
            titleWeight = FontWeight.SemiBold,
            headerCornerRadius = 10.dp,
        )
    }
}

/**
 * 工作区组头标题：唯一允许渲染该文本的入口。
 * COUI 走 preference 标题排版（MiSans + 页面标题色），Miuix 走 footnote1 + onSurfaceVariantSummary。
 */
@Composable
internal fun ConversationSpaceLabel(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
) {
    val tokens = rememberConversationSpaceTokens()
    when (LocalUiStyle.current) {
        AppearanceUiStyle.COUI -> CouiText(
            text = text,
            kind = CouiTextKind.PreferenceTitle,
            color = tokens.titleColor,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
        AppearanceUiStyle.MIUIX -> Text(
            text = text,
            style = MiuixTheme.textStyles.footnote1,
            fontWeight = tokens.titleWeight,
            color = tokens.titleColor,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
    }
}

/** 工作区组头图标：COUI 用 CouiIcon（光学尺寸/权重不同），Miuix 用 Icon。 */
@Composable
internal fun ConversationSpaceGlyph(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = rememberConversationSpaceTokens().iconTint,
) {
    when (LocalUiStyle.current) {
        AppearanceUiStyle.COUI -> CouiIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint,
        )
        AppearanceUiStyle.MIUIX -> Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = modifier,
        )
    }
}

/**
 * 工作区内的会话行标题。
 *
 * COUI 走 `CouiTextKind.Body`（正文光学尺寸），Miuix 走 body1/15sp；选中态在两套风格下都
 * 由背景色 + 色值表达，Miuix 额外加粗，COUI 不叠字重（其字体族本身已有层级）。
 */
@Composable
internal fun ConversationSpaceRowTitle(
    text: String,
    color: Color,
    emphasized: Boolean,
    modifier: Modifier = Modifier,
) {
    when (LocalUiStyle.current) {
        AppearanceUiStyle.COUI -> CouiText(
            text = text,
            kind = CouiTextKind.Body,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
        AppearanceUiStyle.MIUIX -> Text(
            text = text,
            style = MiuixTheme.textStyles.body1.copy(fontSize = 15.sp),
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
    }
}

/** 工作区内的会话行次要行（角色名 / 运行状态）：COUI 走 preference summary，Miuix 走 footnote1/12sp。 */
@Composable
internal fun ConversationSpaceRowCaption(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    when (LocalUiStyle.current) {
        AppearanceUiStyle.COUI -> CouiText(
            text = text,
            kind = CouiTextKind.PreferenceSummary,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
        AppearanceUiStyle.MIUIX -> Text(
            text = text,
            style = MiuixTheme.textStyles.footnote1.copy(fontSize = 12.sp),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
    }
}

/**
 * 组头行内的圆形动作按钮（⋯ / ＋）。
 *
 * COUI 用 couiButtonFeedback 做线性按压反馈，Miuix 用默认 ripple；尺寸与命中区两套一致，
 * 避免两种风格下组头高度不同导致列表位移。
 */
@Composable
internal fun ConversationSpaceAction(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = rememberConversationSpaceTokens()
    val source = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(ConversationSpaceMetrics.ActionButtonSize)
            .clip(CircleShape)
            .then(
                conversationSpaceClickable(
                    interactionSource = source,
                    role = Role.Button,
                    onClick = onClick,
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        ConversationSpaceGlyph(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(ConversationSpaceMetrics.ActionIconSize),
            tint = tokens.actionTint,
        )
    }
}

/**
 * 工作区行级点击反馈：COUI 走 `couiButtonFeedback` 线性高亮（不叠加 ripple），
 * Miuix 走框架默认 indication。
 *
 * 组头、动作按钮、项目选择行统一从这里取，避免出现「有的行有 COUI 按压、有的还是 ripple」。
 */
@Composable
internal fun conversationSpaceClickable(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier {
    val isCoui = LocalUiStyle.current == AppearanceUiStyle.COUI
    return if (isCoui) {
        Modifier
            .couiButtonFeedback(CouiTheme.isDark, interactionSource, enabled = enabled, list = true)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = role,
                onClick = onClick,
            )
    } else {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            enabled = enabled,
            role = role,
            onClick = onClick,
        )
    }
}

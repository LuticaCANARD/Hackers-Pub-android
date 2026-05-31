package pub.hackers.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import pub.hackers.android.R
import pub.hackers.android.ui.HomeFeed
import pub.hackers.android.ui.theme.AppShapes
import pub.hackers.android.ui.theme.LocalAppColors
import pub.hackers.android.ui.theme.LocalAppTypography

@Composable
fun HomeFeedSelector(
    selectedFeed: HomeFeed,
    onFeedSelected: (HomeFeed) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val typography = LocalAppTypography.current
    val density = LocalDensity.current
    var expanded by remember { mutableStateOf(false) }
    var anchorWidthPx by remember { mutableIntStateOf(0) }
    var anchorHeightPx by remember { mutableIntStateOf(0) }
    val selectedLabel = stringResource(selectedFeed.labelResId)
    val stateLabel = stringResource(
        if (expanded) {
            R.string.home_feed_selector_expanded
        } else {
            R.string.home_feed_selector_collapsed
        }
    )
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "homeFeedSelectorChevron",
    )
    val triggerColor by animateColorAsState(
        targetValue = if (expanded) colors.accentMuted.copy(alpha = 0.16f) else colors.surface,
        label = "homeFeedSelectorTrigger",
    )

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .height(AppShapes.pillHeight)
                .clip(RoundedCornerShape(AppShapes.pillRadius))
                .onSizeChanged {
                    anchorWidthPx = it.width
                    anchorHeightPx = it.height
                }
                .clickable { expanded = !expanded }
                .semantics {
                    role = Role.Button
                    contentDescription = selectedLabel
                    stateDescription = stateLabel
                },
            shape = RoundedCornerShape(AppShapes.pillRadius),
            color = triggerColor,
            border = BorderStroke(1.dp, colors.divider),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedLabel,
                    style = typography.bodyLargeSemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(rotation),
                )
            }
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(
                    x = 0,
                    y = anchorHeightPx + with(density) { 6.dp.roundToPx() },
                ),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Surface(
                    shape = RoundedCornerShape(AppShapes.pillRadius),
                    color = colors.surface,
                    border = BorderStroke(1.dp, colors.divider),
                    shadowElevation = 8.dp,
                    modifier = Modifier.widthIn(
                        min = with(density) { anchorWidthPx.toDp() },
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HomeFeed.entries.forEach { feed ->
                            HomeFeedSegment(
                                feed = feed,
                                selected = feed == selectedFeed,
                                onClick = {
                                    expanded = false
                                    onFeedSelected(feed)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeFeedSegment(
    feed: HomeFeed,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalAppColors.current
    val typography = LocalAppTypography.current
    val containerColor by animateColorAsState(
        targetValue = if (selected) colors.accentMuted.copy(alpha = 0.2f) else colors.surface,
        label = "homeFeedChipContainer",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) colors.accent else colors.textSecondary,
        label = "homeFeedSegmentLabel",
    )

    Surface(
        shape = RoundedCornerShape(AppShapes.pillRadius),
        color = containerColor,
        modifier = Modifier
            .defaultMinSize(minHeight = 32.dp)
            .widthIn(min = 72.dp)
            .clip(RoundedCornerShape(AppShapes.pillRadius))
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Button
                this.selected = selected
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(feed.labelResId),
                style = typography.labelMedium,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

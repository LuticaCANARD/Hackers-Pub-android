package pub.hackers.android.ui.screens.news

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import pub.hackers.android.R
import pub.hackers.android.domain.model.NewsOrder
import pub.hackers.android.domain.model.NewsStory
import pub.hackers.android.ui.HomeFeed
import pub.hackers.android.ui.components.ErrorMessage
import pub.hackers.android.ui.components.FullScreenLoading
import pub.hackers.android.ui.components.HomeFeedSelector
import pub.hackers.android.ui.components.LargeTitleHeader
import pub.hackers.android.ui.components.LoadingItem
import pub.hackers.android.ui.components.RichDisplayName
import pub.hackers.android.ui.components.formatRelativeTime
import pub.hackers.android.ui.theme.AppShapes
import pub.hackers.android.ui.theme.LocalAppColors
import pub.hackers.android.ui.theme.LocalAppTypography
import java.net.URI
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    selectedHomeFeed: HomeFeed,
    onHomeFeedSelected: (HomeFeed) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val stories = viewModel.stories.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val colors = LocalAppColors.current

    LaunchedEffect(uiState.selectedOrder) {
        listState.scrollToItem(0)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            LargeTitleHeader(
                title = stringResource(R.string.personal_timeline),
                titleContent = {
                    HomeFeedSelector(
                        selectedFeed = selectedHomeFeed,
                        onFeedSelected = onHomeFeedSelected,
                    )
                },
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(color = colors.surface, shape = CircleShape)
                        .clickable { onSettingsClick() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.nav_settings),
                        tint = colors.accent,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            NewsOrderRow(
                selectedOrder = uiState.selectedOrder,
                onOrderSelected = viewModel::selectOrder,
            )

            HorizontalDivider(color = colors.divider)

            Box(modifier = Modifier.fillMaxSize()) {
                val refresh = stories.loadState.refresh
                when {
                    refresh is LoadState.Error && stories.itemCount == 0 -> {
                        ErrorMessage(
                            message = refresh.error.message ?: stringResource(R.string.error_generic),
                            onRetry = { stories.refresh() },
                        )
                    }

                    stories.itemCount == 0 &&
                        refresh is LoadState.NotLoading &&
                        refresh.endOfPaginationReached -> {
                        ErrorMessage(
                            message = stringResource(R.string.news_empty),
                            onRefresh = { stories.refresh() },
                        )
                    }

                    stories.itemCount == 0 -> {
                        FullScreenLoading()
                    }

                    else -> {
                        PullToRefreshBox(
                            isRefreshing = refresh is LoadState.Loading,
                            onRefresh = { stories.refresh() },
                        ) {
                            LazyColumn(state = listState) {
                                items(
                                    count = stories.itemCount,
                                    key = stories.itemKey { it.id },
                                ) { index ->
                                    val story = stories[index] ?: return@items
                                    NewsStoryCard(
                                        story = story,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    )
                                }

                                if (stories.loadState.append is LoadState.Loading) {
                                    item { LoadingItem() }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsOrderRow(
    selectedOrder: NewsOrder,
    onOrderSelected: (NewsOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        NewsOrder.entries.forEach { order ->
            NewsOrderChip(
                label = stringResource(order.labelResId()),
                selected = order == selectedOrder,
                onClick = { onOrderSelected(order) },
            )
        }
    }
}

@Composable
private fun NewsOrderChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalAppColors.current
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colors.background,
            labelColor = colors.textBody,
            selectedContainerColor = colors.accent,
            selectedLabelColor = colors.background,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = colors.buttonOutline,
            selectedBorderColor = colors.accent,
        ),
    )
}

@Composable
private fun NewsStoryCard(
    story: NewsStory,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val typography = LocalAppTypography.current
    val uriHandler = LocalUriHandler.current
    val domain = remember(story.url) { story.url.displayHost() }
    val activityTime = remember(story.latestActivityAt, story.firstSharedAt) {
        (story.latestActivityAt ?: story.firstSharedAt)?.let { formatRelativeTime(it) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = colors.divider,
                shape = RoundedCornerShape(AppShapes.mediaRadius),
            )
            .clip(RoundedCornerShape(AppShapes.mediaRadius))
            .clickable { uriHandler.openUri(story.url) }
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.title.takeUnless { it.isNullOrBlank() } ?: domain,
                    style = typography.titleMedium,
                    color = colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = domain,
                    style = typography.labelMedium,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            val image = story.image
            if (image != null) {
                Spacer(modifier = Modifier.width(12.dp))
                AsyncImage(
                    model = image.url,
                    contentDescription = image.alt,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        if (!story.description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = story.description,
                style = typography.bodyMedium,
                color = colors.textSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = story.metaLabel(activityTime),
                style = typography.labelMedium,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = stringResource(R.string.news_discussion_count, story.discussionCount),
                style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.accent,
                maxLines = 1,
            )
        }

        val creator = story.creator
        if (creator != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = creator.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(6.dp))
                RichDisplayName(
                    name = creator.name,
                    fallback = creator.handle,
                    style = typography.labelMedium,
                    color = colors.textSecondary,
                    emojiHeight = 14.dp,
                )
            }
        }
    }
}

private fun NewsOrder.labelResId(): Int {
    return when (this) {
        NewsOrder.POPULAR -> R.string.news_order_popular
        NewsOrder.NEWEST -> R.string.news_order_newest
        NewsOrder.ALL_TIME -> R.string.news_order_all_time
    }
}

private fun NewsStory.metaLabel(activityTime: String?): String {
    val parts = mutableListOf<String>()
    parts += String.format(Locale.ROOT, "%.1f", score)
    parts += "$postCount posts"
    if (activityTime != null) parts += activityTime
    return parts.joinToString(" · ")
}

private fun String.displayHost(): String {
    return try {
        URI(this).host?.removePrefix("www.") ?: this
    } catch (_: Exception) {
        this
    }
}

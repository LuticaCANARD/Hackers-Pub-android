package pub.hackers.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pub.hackers.android.ui.theme.LocalAppColors
import pub.hackers.android.ui.theme.LocalAppTypography

@Composable
fun LargeTitleHeader(
    title: String,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
) {
    val colors = LocalAppColors.current
    val typography = LocalAppTypography.current
    val verticalPadding = if (titleContent != null) 8.dp else 12.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (leadingContent != null) 4.dp else 16.dp,
                vertical = verticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingContent != null) {
            leadingContent()
        }
        if (titleContent != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center,
            ) {
                titleContent()
            }
        } else {
            Text(
                text = title,
                style = typography.titleLarge,
                color = colors.textPrimary,
                modifier = if (leadingContent != null) Modifier.padding(start = 4.dp) else Modifier,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (trailingContent != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = trailingContent,
            )
        }
    }
}

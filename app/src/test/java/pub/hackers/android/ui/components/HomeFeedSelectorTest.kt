package pub.hackers.android.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pub.hackers.android.ui.HomeFeed
import pub.hackers.android.ui.theme.AppTypographyDefaults
import pub.hackers.android.ui.theme.LightAppColors
import pub.hackers.android.ui.theme.LocalAppColors
import pub.hackers.android.ui.theme.LocalAppTypography

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeFeedSelectorTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `selector shows chips and selects news`() {
        composeRule.setContent {
            TestTheme {
                var selectedFeed by remember { mutableStateOf(HomeFeed.TIMELINE) }
                HomeFeedSelector(
                    selectedFeed = selectedFeed,
                    onFeedSelected = { selectedFeed = it },
                )
            }
        }

        composeRule.onNodeWithText("Timeline").assertIsDisplayed().performClick()
        composeRule.onAllNodesWithText("News")[0].assertIsDisplayed().performClick()

        composeRule.onNodeWithText("News").assertIsDisplayed()
    }
}

@Composable
private fun TestTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalAppColors provides LightAppColors,
        LocalAppTypography provides AppTypographyDefaults,
    ) {
        MaterialTheme(content = content)
    }
}

package pub.hackers.android.ui.screens.compose

import android.content.Context
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pub.hackers.android.data.repository.HackersPubRepository
import pub.hackers.android.domain.model.ArticleDraft
import pub.hackers.android.domain.model.PublishedArticle
import pub.hackers.android.domain.model.QuotePolicy
import pub.hackers.android.testutil.MainDispatcherRule
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ComposeArticleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<HackersPubRepository>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    @Test
    fun `publishDraft sends selected quote policy`() = runTest {
        coEvery {
            repository.saveArticleDraft(
                title = "Article title",
                content = "Article body",
                tags = emptyList(),
                id = null,
            )
        } returns Result.success(
            ArticleDraft(
                id = "draft-1",
                title = "Article title",
                content = "Article body",
                tags = emptyList(),
                created = Instant.parse("2025-01-01T00:00:00Z"),
                updated = Instant.parse("2025-01-01T00:00:00Z"),
            )
        )
        coEvery {
            repository.publishArticleDraft(
                id = "draft-1",
                slug = "article-title",
                language = any(),
                allowLlmTranslation = true,
                quotePolicy = QuotePolicy.FOLLOWERS,
            )
        } returns Result.success(
            PublishedArticle(id = "article-1", name = "Article title", url = null)
        )

        val vm = ComposeArticleViewModel(repository, context)

        vm.updateTitle("Article title")
        vm.updateContent("Article body")
        vm.updateQuotePolicy(QuotePolicy.FOLLOWERS)
        vm.publishDraft()
        advanceUntilIdle()

        coVerify {
            repository.publishArticleDraft(
                id = "draft-1",
                slug = "article-title",
                language = any(),
                allowLlmTranslation = true,
                quotePolicy = QuotePolicy.FOLLOWERS,
            )
        }
    }
}

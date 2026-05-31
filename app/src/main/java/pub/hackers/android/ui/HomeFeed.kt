package pub.hackers.android.ui

import androidx.annotation.StringRes
import pub.hackers.android.R

enum class HomeFeed(
    val route: String,
    @param:StringRes val labelResId: Int,
) {
    TIMELINE("timeline", R.string.nav_timeline),
    NEWS("news", R.string.nav_news);

    companion object {
        fun fromRoute(route: String?): HomeFeed? {
            return when (route?.substringBefore('?')) {
                TIMELINE.route -> TIMELINE
                NEWS.route -> NEWS
                else -> null
            }
        }
    }
}

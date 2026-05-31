package pub.hackers.android.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeFeedTest {

    @Test
    fun `fromRoute resolves home feed routes`() {
        assertEquals(HomeFeed.TIMELINE, HomeFeed.fromRoute("timeline"))
        assertEquals(HomeFeed.NEWS, HomeFeed.fromRoute("news"))
    }

    @Test
    fun `fromRoute ignores query parameters`() {
        assertEquals(HomeFeed.TIMELINE, HomeFeed.fromRoute("timeline?foo=bar"))
        assertEquals(HomeFeed.NEWS, HomeFeed.fromRoute("news?order=NEWEST"))
    }

    @Test
    fun `fromRoute returns null for unknown routes`() {
        assertNull(HomeFeed.fromRoute(null))
        assertNull(HomeFeed.fromRoute("explore"))
    }
}

package ilhoyu

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpamLinkCheckerTest {

    @Test
    fun checkSpamLink() {
        assertFalse(
            SpamLinkChecker().isSpam("spam spam https://goo.gl/nVLutc", listOf("filekok.com"), 1))

        assertFalse(
            SpamLinkChecker().isSpam("spam spam https://goo.gl/nVLutc", listOf("bit.ly"), 1))

        assertTrue(
            SpamLinkChecker().isSpam("spam spam https://goo.gl/nVLutc", listOf("tvtv24.com"), 2))

        assertFalse(
            SpamLinkChecker().isSpam("spam spam https://goo.gl/nVLutc", listOf("filekok.com"), 2))

        assertFalse(
            SpamLinkChecker().isSpam("spam spam https://goo.gl/nVLutc", listOf("filekok.com"), 3))
    }

    @Test
    fun extractLinks() {
        assertEquals(
            setOf("https://goo.gl/nVLutc"),
            SpamLinkChecker().extractLinksFrom("spam spam https://goo.gl/nVLutc", setOf()))

        assertEquals(
            setOf("https://goo.gl/nVLutc"),
            SpamLinkChecker().extractLinksFrom("spam spam <a href=\"https://goo.gl/nVLutc\">test</a>", setOf()))
    }

}
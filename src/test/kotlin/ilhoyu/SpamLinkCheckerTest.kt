package ilhoyu

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpamLinkCheckerTest {

    @Test
    fun checkSpamLink() {
        assertFalse(
            SpamLinkChecker.isSpam("spam spam https://goo.gl/nVLutc", listOf("filekok.com"), 1))

        assertTrue(
            SpamLinkChecker.isSpam("spam spam https://goo.gl/nVLutc", listOf("bit.ly"), 1))

        assertTrue(
            SpamLinkChecker.isSpam("spam spam https://goo.gl/nVLutc", listOf("tvtv24.com"), 2))

        assertFalse(
            SpamLinkChecker.isSpam("spam spam https://goo.gl/nVLutc", listOf("filekok.com"), 2))

        assertTrue(
            SpamLinkChecker.isSpam("spam spam https://goo.gl/nVLutc", listOf("filekok.com"), 3))
    }

}
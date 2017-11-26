import org.testng.Assert
import org.testng.Assert.assertEquals
import org.testng.annotations.Test
import uk.co.jrtapsell.gpgWrapper.GpgWrapper
import java.io.File

/**
 * @author James Tapsell
 */
class TestGetJrtapsell {
    @Test
    fun testUsernames() {
        val response = Keybase.getByUsername("jrtapsell")!!
        assertEquals(response.usernames["keybase"], "jrtapsell")
        assertEquals(response.usernames["twitter"], "jrtapsell")
        assertEquals(response.usernames["github"], "jrtapsell")
        assertEquals(response.usernames["facebook"], "jrtapsell")
        assertEquals(response.usernames["generic_web_site"], "www.jrtapsell.co.uk")
    }

    @Test
    fun testSignedMessage() {
        val james = Keybase.getByUsername("jrtapsell")!!
        val message = File("../gpgTestFiles/trusted/message.txt").readText()
        val signature = File("../gpgTestFiles/trusted/message.txt.asc").readText()
        GpgWrapper.validate(message, signature, *james.keys.toTypedArray())
    }
}
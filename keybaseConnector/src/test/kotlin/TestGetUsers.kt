import org.testng.Assert
import org.testng.Assert.assertEquals
import org.testng.annotations.Test
import uk.co.jrtapsell.gpgWrapper.GpgWrapper
import java.io.File

/**
 * @author James Tapsell
 */
class TestGetUsers {
    @Test
    fun `Checks test user's data is as expected`() {
        val response = Keybase.getByUsername("jrtapsell")!!
        assertEquals(response.usernames["keybase"], "jrtapsell")
        assertEquals(response.usernames["twitter"], "jrtapsell")
        assertEquals(response.usernames["github"], "jrtapsell")
        assertEquals(response.usernames["facebook"], "jrtapsell")
        assertEquals(response.usernames["generic_web_site"], "www.jrtapsell.co.uk")
    }

}
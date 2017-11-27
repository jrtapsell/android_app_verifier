package uk.co.jrtapsell.fyp.keybaseConnector

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

/**
 * @author James Tapsell
 */
class TestGetUsers {
    @Test
    fun `Checks test user's data is as expected`() {
        val response = Keybase.getByUsername("jrtapsell")!!
        assertEquals(response.usernames["keybaseConnector"], "jrtapsell")
        assertEquals(response.usernames["twitter"], "jrtapsell")
        assertEquals(response.usernames["github"], "jrtapsell")
        assertEquals(response.usernames["facebook"], "jrtapsell")
        assertEquals(response.usernames["generic_web_site"], "www.jrtapsell.co.uk")
    }

}
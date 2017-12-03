package uk.co.jrtapsell.fyp.keybaseConnector

import org.testng.Assert
import org.testng.Assert.assertEquals
import org.testng.annotations.Test
import uk.co.jrtapsell.fyp.keybaseConnector.utils.assertAllEqual
import uk.co.jrtapsell.fyp.keybaseConnector.utils.assertEquals
import uk.co.jrtapsell.fyp.keybaseConnector.utils.only

/**
 * Tests getting users from keybase.
 *
 * @author James Tapsell
 */
class TestGetUsers {

    @Test
    fun `Checks test user's data is as expected`() {
        val response = Keybase.getByUsername("jrtapsell")!!
        response.usernames["keybase"] assertEquals "jrtapsell"
        response.usernames["twitter"] assertEquals "jrtapsell"
        response.usernames["github"] assertEquals "jrtapsell"
        response.usernames["facebook"] assertEquals "jrtapsell"
        response.usernames["generic_web_site"] assertEquals "www.jrtapsell.co.uk"
    }

    @Test
    fun `Checks different sites give equal users`() {
        val keybase = Keybase.getByUsername("jrtapsell")!!
        val github = Keybase.getByGitHub("jrtapsell")!!.only()
        val domain = Keybase.getByDomain("jrtapsell.co.uk")!!.only()
        val website = Keybase.getByDomain("www.jrtapsell.co.uk")!!.only()
        assertAllEqual(keybase, github, domain, website)
    }

    @Test
    fun `Tests getting a missing user`() {
        Assert.assertEquals(Keybase.getByUsername("never_exists"), null)
    }

}
package uk.co.jrtapsell.fyp.keybaseConnector

import org.testng.Assert
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

    /**
     * Gets my user from keybase, and then validates known facts about me.
     */
    @Test
    fun `Checks test user's data is as expected`() {
        val response = Keybase.getByUsername("jrtapsell")!!
        response.usernames["keybase"] assertEquals "jrtapsell"
        response.usernames["twitter"] assertEquals "jrtapsell"
        response.usernames["github"] assertEquals "jrtapsell"
        response.usernames["facebook"] assertEquals "jrtapsell"
        response.usernames["generic_web_site"] assertEquals "www.jrtapsell.co.uk"
    }

    /**
     * Checks equal objects are returned for my various proved accounts.
     */
    @Test
    fun `Checks different sites give equal users`() {
        val keybase = Keybase.getByUsername("jrtapsell")!!
        val github = Keybase.getByGitHub("jrtapsell")!!.only()
        val domain = Keybase.getByDomain("jrtapsell.co.uk")!!.only()
        val website = Keybase.getByDomain("www.jrtapsell.co.uk")!!.only()
        assertAllEqual(keybase, github, domain, website)
    }

    /**
     * Checks that missing users return null.
     */
    @Test
    fun `Tests getting a missing user`() {
        Keybase.getByUsername("never_exists") assertEquals null
    }

}
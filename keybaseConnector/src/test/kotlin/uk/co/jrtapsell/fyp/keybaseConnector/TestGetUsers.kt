package uk.co.jrtapsell.fyp.keybaseConnector

import org.testng.annotations.Test
import uk.co.jrtapsell.fyp.baseUtils.NullUtils.notNull
import uk.co.jrtapsell.fyp.baseUtils.IterableUtils.only
import uk.co.jrtapsell.fyp.baseUtils.TestUtils.assertEquals
import uk.co.jrtapsell.fyp.baseUtils.TestUtils.assertAllEqual

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
        val response = notNull(Keybase.getByUsername("jrtapsell"))
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
        val keybase = notNull(Keybase.getByUsername("jrtapsell"))
        val github = notNull(Keybase.getByGitHub("jrtapsell")).only()
        val domain = notNull(Keybase.getByDomain("jrtapsell.co.uk")).only()
        val website = notNull(Keybase.getByDomain("www.jrtapsell.co.uk")).only()
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
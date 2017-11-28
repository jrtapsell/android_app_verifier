package uk.co.jrtapsell.fyp.keybaseConnector

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

/**
 * @author James Tapsell
 */
class TestGetUsers {
    fun assertAllEqual(vararg items: Any?) {
        items.flatMap{outer -> items.map { outer to it }}.forEach { (a,b) ->
            assertEquals(a, b)
        }
    }

    fun <T> List<T>.only(): T {
        if (size != 1) throw AssertionError("List had size $size")
        return get(0)
    }
    @Test
    fun `Checks test user's data is as expected`() {
        val response = Keybase.getByUsername("jrtapsell")!!
        assertEquals(response.usernames["keybaseConnector"], "jrtapsell")
        assertEquals(response.usernames["twitter"], "jrtapsell")
        assertEquals(response.usernames["github"], "jrtapsell")
        assertEquals(response.usernames["facebook"], "jrtapsell")
        assertEquals(response.usernames["generic_web_site"], "www.jrtapsell.co.uk")
    }

    @Test
    fun `Checks different sites give equal users`() {
        val keybase = Keybase.getByUsername("jrtapsell")!!
        val github = Keybase.getByGitHub("jrtapsell")!!.only()
        val domain = Keybase.getByDomain("jrtapsell.co.uk")!!.only()
        val website = Keybase.getByDomain("www.jrtapsell.co.uk")!!.only()
        assertAllEqual(keybase, github, domain, website)
    }

}
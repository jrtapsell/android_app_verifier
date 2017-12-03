package uk.co.jrtapsell.fyp.keybaseConnector

import org.testng.Assert
import org.testng.annotations.Test

class TestBadRequests {
    @Test
    fun testMissingUser() {
        Assert.assertEquals(Keybase.getByUsername("never_exists"), null)
    }
}
package uk.co.jrtapsell.gitWrapper

import org.testng.Assert
import org.testng.annotations.Test
import uk.co.jrtapsell.gitWrapper.processIO.TerminatedSource

class TestTerminatedSource {

    @Test
    fun `Tests a simple insertion and seal`() {
        val source = TerminatedSource("TERMINATOR")
        source.push("Test 1")
        Assert.assertTrue(source.hasNext())
        Assert.assertEquals(source.next(), "Test 1")
        source.seal()
        Assert.assertFalse(source.hasNext())
    }
}
package uk.co.jrtapsell.fyp.processTools

import org.testng.Assert
import org.testng.annotations.Test

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

    @Test
    fun `Tests in a multithreaded environment`() {

    }
}
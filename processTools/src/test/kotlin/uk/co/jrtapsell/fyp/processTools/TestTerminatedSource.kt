package uk.co.jrtapsell.fyp.processTools

import org.testng.Assert
import org.testng.annotations.Test

/** A sequence of items, which is terminated with a set value. */
class TestTerminatedSource {

    /** Adds an item, reads it back, closes the input and makes sure the source stays valid. */
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
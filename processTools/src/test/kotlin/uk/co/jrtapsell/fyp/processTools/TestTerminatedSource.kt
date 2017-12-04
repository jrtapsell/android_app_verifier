package uk.co.jrtapsell.fyp.processTools

import org.testng.annotations.Test

import uk.co.jrtapsell.fyp.baseUtils.testUtils.*

/** A sequence of items, which is terminated with a set value. */
class TestTerminatedSource {

    /** Adds an item, reads it back, closes the input and makes sure the source stays valid. */
    @Test
    fun `Tests a simple insertion and seal`() {
        val source = TerminatedSource("TERMINATOR")
        source.push("Test 1")
        source.hasNext().assertTrue("Pushed value was not returned")
        source.next() assertEquals "Test 1"
        source.seal()
        source.hasNext().assertFalse()
    }
}
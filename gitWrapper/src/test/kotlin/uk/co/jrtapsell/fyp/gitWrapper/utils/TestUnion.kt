package uk.co.jrtapsell.fyp.gitWrapper.utils

import org.testng.annotations.Test

import uk.co.jrtapsell.fyp.baseUtils.testUtils.*

/**
 * @author James Tapsell
 */
typealias DemoUnion = Union<String, Int>
/** Tests the Union class. */
class TestUnion {

    private val first: DemoUnion = Union.makePrimary("A")
    private val second: DemoUnion = Union.makePrimary("A")
    private val noVal: DemoUnion = Union.makeSecondary(1)

    /** Creates various different unions and checks them. */
    @Test
    fun `Creates and checks simple unions`() {
        first.hasPrimary().assertTrue()
        second.hasPrimary().assertTrue()
        noVal.hasPrimary().assertFalse()
        first assertEquals second
        first assertNotEquals noVal

        assertAllEqual(first.getPrimary(), second.getPrimary(), "A")

        noVal.getSecondary() assertEquals 1
    }

    /** Validates getSecondary. */
    @Test(expectedExceptions = arrayOf(MissingValueException::class))
    fun `Checks that a first valued union has no second value`() = first.getSecondary()

    /** Validates getPrimary. */
    @Test(expectedExceptions = arrayOf(MissingValueException::class))
    fun `Checks that a second valued union has no first value`() = second.getPrimary()
}

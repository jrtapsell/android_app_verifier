package uk.co.jrtapsell.fyp.gitWrapper.utils

import org.testng.Assert
import org.testng.annotations.Test

/**
 * @author James Tapsell
 */
typealias DemoUnion = Union<String, Int>
class TestUnion {

    private val first: DemoUnion = Union.makePrimary("A")
    private val second: DemoUnion = Union.makePrimary("A")
    private val noVal: DemoUnion = Union.makeSecondary(1)

    @Test
    fun `Creates and checks simple unions`() {
        Assert.assertTrue(first.hasPrimary())
        Assert.assertTrue(second.hasPrimary())
        Assert.assertFalse(noVal.hasPrimary())
        Assert.assertEquals(first, second)
        Assert.assertNotEquals(first, noVal)

        Assert.assertEquals(first.getPrimary(), "A")
        Assert.assertEquals(noVal.getSecondary(), 1)
    }

    @Test(expectedExceptions = arrayOf(MissingValueException::class))
    fun `Checks that a first valued union has no second value`() = first.getSecondary()

    @Test(expectedExceptions = arrayOf(MissingValueException::class))
    fun `Checks that a second valued union has no first value`() = second.getPrimary()
}

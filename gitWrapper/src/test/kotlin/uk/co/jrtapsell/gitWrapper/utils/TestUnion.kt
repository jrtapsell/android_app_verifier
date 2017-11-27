package uk.co.jrtapsell.gitWrapper.utils

import org.testng.Assert
import org.testng.annotations.Test

/**
 * @author James Tapsell
 */
typealias DemoUnion = Union<String, Int>
class TestUnion {
    @Test
    fun `Creates and checks simple unions`() {
        val first: DemoUnion = DemoUnion.makePrimary("A")
        val second: DemoUnion = DemoUnion.makePrimary("A")
        val noVal: DemoUnion = DemoUnion.makeSecondary(1)
        Assert.assertTrue(first.hasPrimary())
        Assert.assertTrue(second.hasPrimary())
        Assert.assertFalse(noVal.hasPrimary())
        Assert.assertEquals(first, second)
        Assert.assertNotEquals(first, noVal)

        Assert.assertEquals(first.getPrimary(), "A")
        Assert.assertEquals(noVal.getSecondary(), 1)
    }

    @Test(expectedExceptions = arrayOf(MissingValueException::class))
    fun `Checks that a first valued union has no second value`() {
        val union: DemoUnion = DemoUnion.makePrimary("A")
        union.getSecondary()
    }

    @Test(expectedExceptions = arrayOf(MissingValueException::class))
    fun `Checks that a second valued union has no first value`() {
        val union: DemoUnion = DemoUnion.makeSecondary(1)
        union.getPrimary()
    }

}

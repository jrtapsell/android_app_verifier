package uk.co.jrtapsell.fyp.keybaseConnector.utils

import org.testng.Assert

/** Allows for using infix assertEquals
 *
 * Demo usage:
 *
 * ```1 == 1 assertEquals true```
 */
infix fun Any?.assertEquals(other: Any?) {
    Assert.assertEquals(this, other)
}

/** Asserts that all of the items passed to it are equal to each other. */
fun assertAllEqual(vararg items: Any?) {
    items.flatMap{outer -> items.map { outer to it }}.forEach { (a,b) ->
        Assert.assertEquals(a, b)
    }
}
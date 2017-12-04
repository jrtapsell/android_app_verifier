package uk.co.jrtapsell.fyp.baseUtils.testUtils

import org.testng.Assert

/** Allows for using infix assertEquals
 *
 * Demo usage:
 *
 * ```1 == 1 assertEquals true```
 */
infix fun Any?.assertEquals(expectedValue: Any?) {
    Assert.assertEquals(this, expectedValue)
}

/** Allows for using infix assertNotEquals
 *
 * Demo usage:
 *
 * ```1 == 1 assertNotEquals false```
 */
infix fun Any?.assertNotEquals(expectedValue: Any?) {
    Assert.assertNotEquals(this, expectedValue)
}

/** Checks a value is true.
 *
 * Fails for null or false*/
fun Boolean?.assertTrue(message: String = "Test Failed") {
    if (this != true) {
        Assert.assertTrue(false, message)
    }
}

/** Checks a value is false.
 *
 * Fails for null or true*/
fun Boolean?.assertFalse() {
    if (this != false) {
        Assert.assertFalse(true)
    }
}

/** Checks a value is null. */
fun Any?.assertNull() {
    Assert.assertNull(this)
}

/** Checks a value is not null. */
fun Any?.assertNotNull() {
    Assert.assertNotNull(this)
}

/** Asserts that all of the items passed to it are equal to each other. */
fun assertAllEqual(vararg items: Any?) {
    items.flatMap { outer -> items.map { outer to it } }.forEach { (a, b) ->
        Assert.assertEquals(a, b)
    }
}

fun Any?.fail(message: String) {
    Assert.fail(message)
}

fun <T> notNull(value: T?): T {
    if (value == null) {
        Assert.fail("Value is null")
        throw AssertionError("Value is null")
    } else {
        return value
    }
}
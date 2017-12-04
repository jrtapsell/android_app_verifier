package uk.co.jrtapsell.fyp.baseUtils

import org.testng.Assert

object NullUtils {
    fun <T> notNull(value: T?): T {
        if (value == null) {
            Assert.fail("Value is null")
            throw AssertionError("Value is null")
        } else {
            return value
        }
    }
}
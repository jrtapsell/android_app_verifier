package uk.co.jrtapsell.fyp.keybaseConnector.utils

import org.testng.Assert

infix fun Any?.assertEquals(other: Any?) {
    Assert.assertEquals(this, other)
}


fun assertAllEqual(vararg items: Any?) {
    items.flatMap{outer -> items.map { outer to it }}.forEach { (a,b) ->
        Assert.assertEquals(a, b)
    }
}
package uk.co.jrtapsell.fyp.keybaseConnector.extensions

import kotlin.reflect.KClass

infix fun Class<*>.isA(other: KClass<*>) = other.java.isAssignableFrom(this)
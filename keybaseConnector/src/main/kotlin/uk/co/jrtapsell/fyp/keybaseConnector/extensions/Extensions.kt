package uk.co.jrtapsell.fyp.keybaseConnector.extensions

import kotlin.reflect.KClass

/** Runtime check if this class is a subclass of another class. */
infix fun Class<*>.isA(other: KClass<*>) = other.java.isAssignableFrom(this)
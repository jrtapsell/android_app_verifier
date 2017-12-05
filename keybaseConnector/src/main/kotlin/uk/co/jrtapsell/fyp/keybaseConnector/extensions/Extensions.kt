package uk.co.jrtapsell.fyp.keybaseConnector.extensions

import org.json.JSONArray
import kotlin.reflect.KClass

/** Runtime check if this class is a subclass of another class. */
infix fun Class<*>.isA(other: KClass<*>) = other.java.isAssignableFrom(this)

fun <T: Any> JSONArray.applyEach(block: JSONArray.(Int) -> T?) =
    (0 until length()).mapNotNull { block(it) }
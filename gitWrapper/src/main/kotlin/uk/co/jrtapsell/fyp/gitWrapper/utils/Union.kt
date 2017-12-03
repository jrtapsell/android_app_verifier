package uk.co.jrtapsell.fyp.gitWrapper.utils

/** Exception that is thrown if an attempt is made to retrieve a missing value. */
class MissingValueException(message: String): RuntimeException(message)

/** A class that represents one of 2 options. */
data class Union<out T : Any, out U : Any> private constructor(
        private val primary: T?,
        private val secondary: U?) {

    companion object {
        /** Makes a union with the given primary value. */
        fun <T : Any> makePrimary(value: T) = Union(value, null)
        /** Makes a union with the given secondary value. */
        fun <U : Any> makeSecondary(value: U) = Union(null, value)
    }

    /** Checks if a union has a primary value. */
    fun hasPrimary(): Boolean = primary != null

    /** Gets the primary value of this union, or throws a #MissingValueException. */
    fun getPrimary(): T = primary?:throw MissingValueException("No primary value")
    /** Gets the secondary value of this union, or throws a #MissingValueException. */
    fun getSecondary(): U = secondary?: throw MissingValueException("No secondary value")
}
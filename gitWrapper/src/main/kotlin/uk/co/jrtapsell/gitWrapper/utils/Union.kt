package uk.co.jrtapsell.gitWrapper.utils

data class Union<out T: Any, out U: Any> private constructor(private val primary: T?, private val secondary: U?) {

    companion object {
        fun <T: Any, U: Any> makePrimary(value: T): Union<T, U> {
            return Union(value, null)
        }
        fun <T: Any, U: Any> makeSecondary(value: U): Union<T, U> {
            return Union(null, value)
        }
    }

    fun hasPrimary(): Boolean = primary != null

    fun getPrimary(): T = primary!!
    fun getSecondary(): U = secondary!!
}
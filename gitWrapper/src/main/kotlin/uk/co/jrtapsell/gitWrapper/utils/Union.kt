package uk.co.jrtapsell.gitWrapper.utils

data class Union<out T : Any, out U : Any> private constructor(
        private val primary: T?,
        private val secondary: U?) {

    companion object {
        fun <T : Any> makePrimary(value: T) = Union(value, null)
        fun <U : Any> makeSecondary(value: U) = Union(null, value)
    }

    fun hasPrimary(): Boolean = primary != null

    fun getPrimary(): T = primary!!
    fun getSecondary(): U = secondary!!
}
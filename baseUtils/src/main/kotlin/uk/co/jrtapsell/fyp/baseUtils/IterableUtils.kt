package uk.co.jrtapsell.fyp.baseUtils

/**
 * @author James Tapsell
 */
object IterableUtils {
    fun <T> List<T>.only(): T {
        if (size != 1) throw AssertionError("List had size $size")
        return get(0)
    }
}
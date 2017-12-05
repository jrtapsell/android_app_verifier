package uk.co.jrtapsell.fyp.baseUtils

/**
 * @author James Tapsell
 */
object IterableUtils {
    /** Checks the list has only one item, and if so returns it, otherwise throws an asserion error. */
    fun <T> List<T>.only(): T {
        if (size != 1) throw AssertionError("List had size $size")
        return get(0)
    }
}
package uk.co.jrtapsell.fyp.keybaseConnector.utils

/**
 * @author James Tapsell
 */


fun <T> List<T>.only(): T {
    if (size != 1) throw AssertionError("List had size $size")
    return get(0)
}
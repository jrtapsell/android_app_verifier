package uk.co.jrtapsell.fyp.processTools

import java.util.concurrent.LinkedBlockingDeque

/** Iterator that can keep having stuff added to it until it is closed. */
class TerminatedSource<T: Any>(private val terminator: T): Iterator<T> {
    private val backing = LinkedBlockingDeque<T>()

    private var _last: T? = null

    private val last: T get() {
            if (_last == null) {
                _last = backing.take()
            }
            return checkNotNull(_last)
    }

    /** Seals the source, saying no new items will be added */
    fun seal() = push(terminator)

    /** Pushes the item into the source. */
    fun push(line: T) = backing.put(line)

    /** Gets the next value from the source. */
    override fun next(): T {
        val line = last
        _last = null
        return line
    }

    /** Checks if the source has a next item. */
    override fun hasNext(): Boolean {
        return last != terminator
    }
}
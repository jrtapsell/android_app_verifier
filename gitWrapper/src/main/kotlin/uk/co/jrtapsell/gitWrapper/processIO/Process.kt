package uk.co.jrtapsell.gitWrapper.processIO

import java.io.File
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.Scanner
import kotlin.concurrent.thread


/** An output line/ */
data class Line(val text: String, val stream: IOStream) {
    /** Stream type for the line. */
    enum class IOStream {
        /** System.out */ OUT,
        /** System.err */ ERR,
        /** Value to say that stream is done. */ STOP }
}

/** Iterator that can keep having stuff added to it until it is closed. */
class TerminatedSource<T>(private val terminator: T): Iterator<T> {
    private val backing = LinkedBlockingDeque<T>()

    private var _last: T? = null
    private val last: T get() {
            if (_last == null) {
                _last = backing.take()!!
            }
            return _last!!
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
        return last == terminator
    }
}

/**
 * Represents a created process that has already started.
 */
class OutputSequence(
        private val process: Process,
        private val waitFor: Boolean) : Sequence<Line>, AutoCloseable {

    private val store = TerminatedSource(Line("", Line.IOStream.STOP))

    /** Waits for the process to stop, killing it if asked to. */
    override fun close() {
        if (!waitFor) {
            process.destroy()
            process.waitFor(1, TimeUnit.SECONDS)
            if (process.isAlive) {
                process.destroyForcibly()
            }
        }
        exitCode = process.waitFor()
    }

    /**
     * Gets the exit code of the process, starts as null,
     * then changes to a value once the process is done.
     */
    var exitCode: Int? = null

    private val hashCode = System.identityHashCode(process)
    private val err = thread(name = "stdErr for P$hashCode", block = {
        Scanner(process.errorStream).forEach {
            store.push(Line(it, Line.IOStream.ERR))
        }
    })

    private val out = thread(name = "stdOut for P$hashCode", block = {
        Scanner(process.inputStream).forEach {
            store.push(Line(it, Line.IOStream.OUT))
        }
    })

    init {
        process.onExit().thenAccept {
            out.join()
            err.join()
            store.seal()
        }
    }

    /** Gets the iterator of lines for the process output. */
    override fun iterator() = store

    /** Represents the OutputSource as a string. */
    override fun toString() = "Process P$hashCode"
}

/**
 * Starts a process with the given parameters
 *
 * @param waitFor
 *   If true, closing waits for the process to halt, otherwise it just kills it
 * @param workingDir
 *   The working directory for the process
 * @param command
 *   The command to run
 */
fun run(waitFor: Boolean, workingDir: String, vararg command: String): OutputSequence {
    val process = ProcessBuilder()
            .directory(File(workingDir))
            .command(*command)
            .start()!!
    return OutputSequence(process, waitFor)
}
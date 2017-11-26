package uk.co.jrtapsell.gitWrapper.processIO

import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.Scanner
import extensions._Thread.thread
import java.io.PrintStream

/** An output line/ */
data class Line(val text: String, val stream: IOStream) {
    /** Stream type for the line. */
    enum class IOStream {
        /** System.out */ OUT,
        /** System.err */ ERR,
        /** Value to say that stream is done. */ STOP }
}

/**
 * Represents a created process that has already started.
 */
class OutputSequence(
        private val process: Process,
        private val waitFor: Boolean) : Sequence<Line>, AutoCloseable {

    private val store = TerminatedSource(Line("", Line.IOStream.STOP))

    private val input = PrintStream(process.outputStream)

    private val lineLog = mutableListOf<Line>()

    fun inputLine(line: String) = input.println(line)

    /** Waits for the process to stop, killing it if asked to. */
    override fun close() {
        if (!waitFor) {
            process.destroy()
            process.waitFor(1, TimeUnit.SECONDS)
            if (process.isAlive) {
                System.err.println("Having to kill process hard, something went wrong")
                process.destroyForcibly()
            }
        }
        exitCode = process.waitFor()
        input.close()
        out.join()
        err.join()
    }

    /**
     * Gets the exit code of the process, starts as null,
     * then changes to a value once the process is done.
     */
    var exitCode: Int? = null

    private val hashCode = System.identityHashCode(process)

    private val group = ThreadGroup("P$hashCode")

    private fun makeThread(name: String, stream: InputStream, type: Line.IOStream): Thread {
        return thread(group, name) {
            val scanner = Scanner(stream)
            while (scanner.hasNextLine()) {
                val line = Line(scanner.nextLine(), type)
                store.push(line)
                lineLog.add(line)
            }
        }
    }

    private val err = makeThread(
            "P$hashCode.stdErr",
            process.errorStream,
            Line.IOStream.ERR)

    private val out = makeThread(
            "P$hashCode.stdOut",
            process.inputStream,
            Line.IOStream.OUT)

    init {
        thread(group, "P$hashCode.Exit") {
            process.waitFor()
            out.join()
            err.join()
            store.seal()
        }
    }

    /** Gets the iterator of lines for the process output. */
    override fun iterator() = store

    /** Represents the OutputSource as a string. */
    override fun toString() = "Process P$hashCode"

    fun closeInput() {
        input.close()
    }

    fun assertClosedCleanly() {
        if (exitCode != 0) {
            val errorMessage = lineLog.joinToString(System.lineSeparator()){ it.text }
            throw AssertionError("Exit code was not 0:\n$errorMessage")
        }
    }
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
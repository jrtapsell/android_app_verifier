package uk.co.jrtapsell.gitWrapper

import uk.co.jrtapsell.gitWrapper.data.Commit
import uk.co.jrtapsell.gitWrapper.processIO.Line
import uk.co.jrtapsell.gitWrapper.processIO.run

class Git(val directory: String) {
    fun listCommits(): List<Commit> {
        val process = run(
                true,
                directory,
                "git", "log", "--all", "--pretty=${Commit.PRETTY_STRING}")

        val back = process.use {
            it.map {
                if (it.stream == Line.IOStream.ERR) it.text else Commit.convert(it.text)
            }
        }.toList()
        val errLines = back.filter { it is String }.map { it as String }
        if (errLines.isNotEmpty()) {
            val message = errLines.joinToString("\n")
            throw GitException("Git output error message\n$message")
        }
        if (process.exitCode != 0) {
            throw GitException("Git returned ${process.exitCode}")
        }
        return back.map { it as Commit }
    }

}
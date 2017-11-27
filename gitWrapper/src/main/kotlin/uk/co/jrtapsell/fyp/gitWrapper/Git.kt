package uk.co.jrtapsell.fyp.gitWrapper

import uk.co.jrtapsell.fyp.gitWrapper.data.Commit
import uk.co.jrtapsell.fyp.gitWrapper.data.SignatureStatus
import uk.co.jrtapsell.fyp.processTools.Line
import uk.co.jrtapsell.fyp.processTools.run
import uk.co.jrtapsell.fyp.gitWrapper.utils.Union
import java.io.IOException

typealias CommitOrString = Union<Commit, String>

class Git(private val directory: String) {
    fun listCommits(): List<Commit> {
        val process = try {
            run(
                    true,
                    directory,
                    "git", "log", "--all", "--pretty=${Commit.PRETTY_STRING}")

        } catch (ex: IOException) {
            val message = ex.message
            throw GitException("Couldn't list commits, " + when {
                message == null -> "Unknown error"
                message.contains("Not a directory") -> "$directory is not a directory"
                else -> "Cannot run git in $directory, unknown error"
            })
        }
        val back: List<CommitOrString> = process.use {
            it.map {
                if (it.stream == Line.IOStream.ERR) {
                    CommitOrString.makeSecondary(it.text)
                } else {
                    CommitOrString.makePrimary(Commit.convert(it.text))
                }
            }
        }.toList()
        val errLines = back.filter { !it.hasPrimary() }.map { it.getSecondary() }
        if (errLines.isNotEmpty()) {
            val message = errLines.joinToString("\n").trim()
            if (message == "fatal: Not a git repository (or any of the parent directories): .git") {
                throw GitException("Couldn't list commits, $directory is not a git repo")
            }
            throw GitException("Couldn't list commits, git output error message:\n$message")
        }
        if (process.exitCode != 0) {
            throw GitException("Couldn't list commits, git returned ${process.exitCode}")
        }
        return back.map { it.getPrimary() }
    }

    fun getState(): SignatureStatus = this.listCommits()
            .map { it.signer?.status?: SignatureStatus.UNSIGNED }
            .max() ?: SignatureStatus.UNSIGNED

}
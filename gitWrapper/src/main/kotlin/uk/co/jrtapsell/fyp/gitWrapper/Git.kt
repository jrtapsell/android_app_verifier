package uk.co.jrtapsell.fyp.gitWrapper

import uk.co.jrtapsell.fyp.gitWrapper.data.Commit
import uk.co.jrtapsell.fyp.gitWrapper.data.SignatureStatus
import uk.co.jrtapsell.fyp.processTools.Line
import uk.co.jrtapsell.fyp.processTools.run
import uk.co.jrtapsell.fyp.gitWrapper.utils.Union
import java.io.IOException

/** Alias used the union of either an error string or a commit. */
typealias CommitOrString = Union<Commit, String>

/** Wraps around the git executable. */
class Git(private val directory: String) {
    /** Lists all commits in the current repository. */
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
            if (message.startsWith("fatal: Not a git repository")) {
                throw GitException("Couldn't list commits, $directory is not a git repo")
            }
            throw GitException("Couldn't list commits, git output error message:\n$message")
        }
        if (process.exitCode != 0) {
            throw GitException("Couldn't list commits, git returned ${process.exitCode}")
        }
        return back.map { it.getPrimary() }
    }

    /** Gets the weakest state of the commits current repository. */
    fun getState(): SignatureStatus = this.listCommits()
            .map { it.signer?.status?: SignatureStatus.UNSIGNED }
            .max() ?: SignatureStatus.UNSIGNED

}
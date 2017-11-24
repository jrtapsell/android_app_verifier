package uk.co.jrtapsell.gpgWrapper

import uk.co.jrtapsell.gitWrapper.processIO.Line
import uk.co.jrtapsell.gitWrapper.processIO.run

data class SignatureStatus(val valid: Boolean, val trusted: Boolean)
object GpgWrapper {
    fun validate(message: String, signature: String): SignatureStatus {

        val process = run (true, "/", "gpg", "-d")

        signature.lines().forEach {
            process.inputLine(it)
        }
        process.closeInput()

        val lines = process.toList()

        val signedMessage = lines.filter { it.stream == Line.IOStream.OUT }.map { it.text }

        val signature = lines.filter { it.stream == Line.IOStream.ERR }.map { it.text }

        val signatureRegexes = listOf(
                Regex("gpg: Signature made (.*) using (.*) key ID (.*)"),
                Regex("gpg: Good signature from (.*)")
        )
        val valid = signature.zip(signatureRegexes).all { (line, regex) ->
            regex.matchEntire(line) != null
        }
        val sameMessage = message.lines() == signedMessage

        val goodSig = valid && sameMessage
        return SignatureStatus(goodSig, goodSig)
    }

    fun sign(message: String): String {
        val process = run(true, "/", "gpg", "-armor", "-s")
        message.lines().forEach {
            process.inputLine(it)
        }
        process.closeInput()
        return process.filter {
            it.stream == Line.IOStream.OUT
        }.map {
            it.text
        }.joinToString(System.lineSeparator())
    }
}
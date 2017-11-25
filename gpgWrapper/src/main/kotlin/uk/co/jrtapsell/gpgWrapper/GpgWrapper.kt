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
        val (signatureState, trust) = signature.zip(signatureRegexes).map { (line, regex) ->
            regex.matchEntire(line) != null
        }
        val sameMessage = message.lines() == signedMessage

        return SignatureStatus(sameMessage && signatureState, sameMessage && trust)
    }

    fun sign(message: String): String {
        val command = arrayOf(
                "gpg",
                "--passphrase",
                System.getenv("JRT_GPG_PASSWORD")?:"UNSET",
                "--armor",
                "-s")
        val process = run(true, "/", *command)
        message.lines().forEach {
            process.inputLine(it)
        }
        process.closeInput()
        process.use{}
        val lines = process.toList()
        process.assertClosedCleanly()
        return lines.filter {
            it.stream == Line.IOStream.OUT
        }.map {
            it.text
        }.joinToString(System.lineSeparator())
    }
}
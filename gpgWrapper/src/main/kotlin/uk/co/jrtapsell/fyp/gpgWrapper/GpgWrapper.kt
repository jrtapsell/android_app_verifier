package uk.co.jrtapsell.fyp.gpgWrapper

import uk.co.jrtapsell.fyp.processTools.Line
import uk.co.jrtapsell.fyp.processTools.run
import java.io.File

data class SignatureStatus(val valid: Boolean, val trusted: Boolean)

class MockCloseable: AutoCloseable {
    override fun close() {}
}

object GpgWrapper {
    fun validate(message: String, signature: String, vararg signatureKey: GpgKey = arrayOf()): SignatureStatus {

        val (command, key) = if (signatureKey.isEmpty()) {
            arrayOf("gpg",
                    "-d") to MockCloseable()
        } else {
            val temp = File.createTempFile("key", ".asc")
            temp.appendText(GpgKey.dearmor(*signatureKey))
            arrayOf("gpg",
                    "--no-default-keyring",
                    "--keyring",
                    temp.canonicalPath,
                    "-d") to AutoCloseable { temp.delete() }
        }

        val process = run(true, "/", *command)

        signature.lines().forEach {
            process.inputLine(it)
        }
        process.closeInput()

        process.use {}
        val lines = process.toList()
        key.close()

        val signedMessage = lines.filter { it.stream == Line.IOStream.OUT }.map { it.text }

        val signatureText = lines.filter { it.stream == Line.IOStream.ERR }.map { it.text }

        val signatureRegexes = listOf(
                Regex("gpg: Signature made (.*) using (.*) key ID (.*)"),
                Regex("gpg: Good signature from (.*)")
        )
        val (signatureState, trust) = signatureText.zip(signatureRegexes).map { (line, regex) ->
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
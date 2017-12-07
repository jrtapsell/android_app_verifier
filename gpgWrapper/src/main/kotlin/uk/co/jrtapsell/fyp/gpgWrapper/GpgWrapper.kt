package uk.co.jrtapsell.fyp.gpgWrapper

import uk.co.jrtapsell.fyp.processTools.Line
import uk.co.jrtapsell.fyp.processTools.run
import java.io.File

/** Represents the status of a signature. */
data class SignatureStatus(val valid: Boolean, val trusted: Boolean)

/** Mock closeable that does nothing. */
class MockCloseable: AutoCloseable {
    /** Does nothing. */
    override fun close() {}
}

/** Wraps the GPG executable and allows operations to be performed on it. */
object GpgWrapper {
    /** Validates that a message was signed with a given key.
     *
     * @param message
     *  The message to validate
     * @param signature
     *  The signature to validate
     * @param signatureKey
     *  The keyring to use for validation (or null to use the system keyring)
     */
    fun validate(message: String, signature: String, signatureKey: List<GpgKey> = listOf()): SignatureStatus {

        val (command, key) = if (signatureKey.isEmpty()) {
            listOf("gpg",
                    "-d") to MockCloseable()
        } else {
            val temp = File.createTempFile("key", ".asc")
            val darmored = GpgKey.dearmor(signatureKey)
            temp.writeText(darmored)
            println(temp.canonicalPath)
            listOf("gpg",
                    "--no-default-keyring",
                    "--keyring",
                    temp.canonicalPath,
                    "-d") to AutoCloseable { temp.delete() }
        }

        val process = run(true, "/", command)

        signature.lines().forEach {
            process.inputLine(it)
        }
        process.closeInput()

        process.use {}
        val lines = process.toList()
        for (i in lines) {
            println(i)
        }
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

    /** Signs a message with the default key. */
    fun sign(message: String): String {
        val command = arrayOf(
                "gpg",
                "--passphrase",
                System.getenv("JRT_GPG_PASSWORD")?:"UNSET",
                "--armor",
                "-s")
        val process = run(true, "/", command.asList())
        message.lines().forEach {
            process.inputLine(it)
        }
        process.closeInput()
        process.use{}
        val lines = process.toList()
        process.assertClosedCleanly()
        return lines.filter {
            it.stream == Line.IOStream.OUT
        }.joinToString(System.lineSeparator()) {
            it.text
        }
    }
}
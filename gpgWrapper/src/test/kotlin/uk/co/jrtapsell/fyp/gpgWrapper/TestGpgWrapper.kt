package uk.co.jrtapsell.fyp.gpgWrapper

import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import uk.co.jrtapsell.fyp.baseUtils.testUtils.*

import java.io.File

/** Utility method to get the contents of a resource file. */
fun getResource(name: String) =
    File(name).useLines { it.toList() }.joinToString(System.lineSeparator())

/** Data class for a test message. */
data class Testmessage(
    val messagePath: String,
    val sigPath: String,
    val signatureStatus: SignatureStatus)

/** Provides test GPG files. */
class TestGpgWrapper {
    @DataProvider(name = "messages")
    /** Provides a set of messages and their signatures for use in tests. */
    fun message(): Array<Testmessage> {
        return arrayOf(
                Testmessage("../gpgTestFiles/trusted/message.txt",
                        "../gpgTestFiles/trusted/message.txt.asc",
                        SignatureStatus(true, true)),
                Testmessage("../gpgTestFiles/badMix/message.txt",
                        "../gpgTestFiles/badMix/message.txt.asc",
                        SignatureStatus(false, false)),
                Testmessage("../gpgTestFiles/untrusted/message.txt",
                        "../gpgTestFiles/untrusted/message.txt.asc",
                        SignatureStatus(true, false))
        )
    }

    /** Checks the signature status of each of the test messages. */
    @Test(dataProvider = "messages")
    fun `Checks various stored signatures `(test: Testmessage) {
        val message = getResource(test.messagePath)
        val signature = getResource(test.sigPath)
        val sigState = GpgWrapper.validate(message, signature)
        sigState assertEquals  test.signatureStatus
    }

    /** Attempts to sign a message, and then uses the validator to make sure the returned
     * signature is valid.
     */
    @Test(enabled = false)
    fun `Tries to sign a message, and checks it is signed`() {
        val helloWorld = "Hello World"
        val signature = GpgWrapper.sign(helloWorld)
        GpgWrapper.validate(helloWorld, signature).valid.assertTrue()
    }
}

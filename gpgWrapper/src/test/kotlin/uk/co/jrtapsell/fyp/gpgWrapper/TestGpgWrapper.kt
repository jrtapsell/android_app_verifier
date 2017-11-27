package uk.co.jrtapsell.fyp.gpgWrapper

import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.io.File

fun getResource(name: String) =
    File(name).useLines { it.toList() }.joinToString(System.lineSeparator())

data class Testmessage(
    val messagePath: String,
    val sigPath: String,
    val signatureStatus: SignatureStatus)

class TestGpgWrapper {
    @DataProvider(name = "messages")
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

    @Test(dataProvider = "messages")
    fun `Checks various signatures`(test: Testmessage) {
        val message = getResource(test.messagePath)
        val signature = getResource(test.sigPath)
        val sigState = GpgWrapper.validate(message, signature)
        Assert.assertEquals(sigState, test.signatureStatus)
    }

    @Test(enabled = false)
    fun `Tries to sign a message, and checks it is signed`() {
        val helloWorld = "Hello World"
        val signature = GpgWrapper.sign(helloWorld)
        Assert.assertTrue(GpgWrapper.validate(helloWorld, signature).valid)
    }
}

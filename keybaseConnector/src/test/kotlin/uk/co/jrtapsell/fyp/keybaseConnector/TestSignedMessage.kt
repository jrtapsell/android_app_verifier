package uk.co.jrtapsell.fyp.keybaseConnector

import org.testng.annotations.Test
import java.io.File

class TestSignedMessage {

    @Test
    fun `Checks messages signed by various users are correct`() {
        val james = KeybaseVerifier("jrtapsell")
        val message = File("../gpgTestFiles/trusted/message.txt").inputStream()
        val signature = File("../gpgTestFiles/trusted/message.txt.asc").inputStream()
        james.verifySigned(signature, message)
    }
}
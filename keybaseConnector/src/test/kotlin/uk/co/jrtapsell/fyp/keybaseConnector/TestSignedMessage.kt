package uk.co.jrtapsell.fyp.keybaseConnector

import org.testng.annotations.Test
import java.io.File

/** Tests messages sigend by keybase users are trusted. */
class TestSignedMessage {

    /** Uses a stored message signed by me to test. */
    @Test
    fun `Checks a message that is signed by me is trusted`() {
        val james = KeybaseVerifier("jrtapsell")
        val message = File("../gpgTestFiles/trusted/message.txt").inputStream()
        val signature = File("../gpgTestFiles/trusted/message.txt.asc").inputStream()
        james.verifySigned(signature, message)
    }

    /** Uses a stored message signed by a different user to test. */
    @Test
    fun `Checks a message that is signed by another user is trusted`() {
        val evil = KeybaseVerifier("evil_morty")
        val message = File("../gpgTestFiles/evil/message.txt").inputStream()
        val signature = File("../gpgTestFiles/evil/message.txt.asc").inputStream()
        evil.verifySigned(signature, message)
    }

    /** Tries to validate a message against a different user. */
    @Test(expectedExceptions = arrayOf(KeybaseException::class))
    fun `Checks that messages are not trusted with the wrong user`() {
        val evil = KeybaseVerifier("evil_morty")
        val message = File("../gpgTestFiles/trusted/message.txt").inputStream()
        val signature = File("../gpgTestFiles/trusted/message.txt.asc").inputStream()
        evil.verifySigned(signature, message)
    }

    /** Tries to validate rubbish. */
    @Test(expectedExceptions = arrayOf(KeybaseException::class))
    fun `Checks that rubbish messages are not allowed`() {
        val evil = KeybaseVerifier("evil_morty")
        val message = File("../gpgTestFiles/garbage/message.txt").inputStream()
        val signature = File("../gpgTestFiles/garbage/message.txt.asc").inputStream()
        evil.verifySigned(signature, message)
    }
}
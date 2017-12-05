package uk.co.jrtapsell.fyp.keybaseConnector

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfig
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator
import org.jetbrains.annotations.Contract
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL

/** Wraps Bouncy Castle to verify that a Keybase user signed a given message. */
data class KeybaseVerifier(val username: String) {
    private val config = KeybaseConfig(username)

    /** Verifies the signature stream is a valid trusted signature of the message scheme. */
    fun verifySigned(signature: InputStream, message: InputStream) {
        try {
            val input = BouncyGPG.decryptAndVerifyStream()
                .withConfig(config)
                .andValidateSomeoneSigned()
                .fromEncryptedInputStream(PGPUtil.getDecoderStream(signature))

            var temp = 0
            while (temp != -1) {
                val sig = input.read()
                val mes = message.read()
                if (sig != mes) {
                    throw AssertionError("Non matching input")
                }
                temp = sig
            }
        } catch (ex: Exception) {
            KeybaseException.rethrow(ex)
        }
    }
}

/** A #KeyringConfig that trusts the keys listed for a given keybase user. */
data class KeybaseConfig(val username: String): KeyringConfig {
    private lateinit var decoded: PGPPublicKeyRingCollection
    private lateinit var keyFingerprints: List<String>

    init {
        URL("https://keybase.io/$username/pgp_keys.asc").openConnection().let { connection ->
            connection.connect()
            val rawInput = connection.getInputStream()
            val opened = PGPUtil.getDecoderStream(rawInput)
            decoded = JcaPGPPublicKeyRingCollection(opened)
            rawInput.close()
            keyFingerprints = decoded.flatMap { it }
                .map { it.fingerprint.toHexString() }
        }
    }

    /** Gets the public keys for the user. */
    override fun getPublicKeyRings() = decoded
    /** Gets the fingerprint calculator. */
    override fun getKeyFingerPrintCalculator() = BcKeyFingerprintCalculator()

    /** Gets the password for a given key (always throws AssertionError). */
    @Contract("_ -> fail")
    override fun decryptionSecretKeyPassphraseForSecretKeyId(keyID: Long) = throw AssertionError(keyID)

    /** Gets the secrey keys (always throws AssertionError). */
    @Contract(" -> fail")
    override fun getSecretKeyRings() = throw AssertionError()
}

private fun ByteArray.toHexString(): String {
    return this.joinToString("") {
        it.toInt().and(0xff).toString(16).padStart(2, '0').toUpperCase()
    }
}
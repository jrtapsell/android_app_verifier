package uk.co.jrtapsell.fyp.keybaseConnector

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfig
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.jcajce.JcaPGPPublicKeyRingCollection
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL

data class KeybaseVerifier(val username: String) {
    private val config = KeybaseConfig(username)

    fun verifySigned(signature: InputStream, message: InputStream) {
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
    }
}

class KeybaseConfig(username: String): KeyringConfig {
    lateinit var decoded: PGPPublicKeyRingCollection
    lateinit var keyFingerprints: List<String>

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

    override fun getPublicKeyRings() = decoded
    override fun getKeyFingerPrintCalculator() = BcKeyFingerprintCalculator()

    override fun decryptionSecretKeyPassphraseForSecretKeyId(keyID: Long) = throw AssertionError(keyID)
    override fun getSecretKeyRings() = throw AssertionError()
}

private fun ByteArray.toHexString(): String {
    return this.joinToString("") {
        it.toInt().and(0xff).toString(16).padStart(2, '0').toUpperCase()
    }
}
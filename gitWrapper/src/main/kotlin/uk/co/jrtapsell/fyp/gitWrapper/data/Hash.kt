package uk.co.jrtapsell.fyp.gitWrapper.data

import uk.co.jrtapsell.fyp.gitWrapper.GitException
import java.util.*

/**
 * Represents a git hash.
 *
 * @author James Tapsell
 */
class Hash(private val data:ByteArray) {
    /** Represents the hash as a String. */
    override fun toString() = value()

    private fun Byte.unsigned(): Int = this.toInt() and 0xFF
    private fun Byte.toHex(length: Int) = this.unsigned().toString(16).padStart(length, '0')

    /** Gets the hash as a hex string. */
    fun value(): String {
        return data.joinToString("") { it.toHex(2) }
    }

    /** Gets the last n characters of the commit hash. */
    fun last(length: Int): String {
        return data.takeLast((length + 1) / 2)
                .joinToString("") {it.toHex(2)}
                .takeLast(length)
    }

    /** Checks this object against another for equality. */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Hash) return false

        return Arrays.equals(data, other.data)
    }

    /** Hashes this hash. */
    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }

    companion object {
        /** Reads the hash from a string. */
        fun fromString(hash: String): Hash {
            if (hash.isBlank()) throw GitException("Missing hash")

            val out = ByteArray(20)
            (0 until 20).forEach {
                val byteText = hash.substring(2 * it, 2 * it + 2)
                val intValue = byteText.toInt(16)
                val byteVal = intValue.toByte()
                out[it] = byteVal
            }
            return Hash(out)
        }
    }
}
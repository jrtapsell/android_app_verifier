package uk.co.jrtapsell.gitWrapper.data

import java.util.*

/**
 * @author James Tapsell
 */
class Hash(private val data:ByteArray) {
    override fun toString() = value()

    fun Byte.unsigned(): Int = this.toInt() and 0xFF
    fun Byte.toHex(length: Int) = this.unsigned().toString(16).padStart(length, '0')

    private fun value(): String {
        return data.joinToString("") { it.toHex(2) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hash

        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }

    companion object {
        fun fromString(hash: String): Hash? {
            if (hash.isBlank()) return null

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
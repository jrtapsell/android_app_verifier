package uk.co.jrtapsell.fyp.gitWrapper.data

import java.util.*

/**
 * @author James Tapsell
 */
class Hash(private val data:ByteArray) {
    override fun toString() = value()

    private fun Byte.unsigned(): Int = this.toInt() and 0xFF
    private fun Byte.toHex(length: Int) = this.unsigned().toString(16).padStart(length, '0')

    fun value(): String {
        return data.joinToString("") { it.toHex(2) }
    }

    fun last(length: Int): String {
        return data.takeLast((length + 1) / 2)
                .joinToString("") {it.toHex(2)}
                .takeLast(length)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Hash) return false

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
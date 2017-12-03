package uk.co.jrtapsell.fyp.gpgWrapper

import uk.co.jrtapsell.fyp.processTools.Line
import uk.co.jrtapsell.fyp.processTools.run

/** Represents a GPG key. */
data class GpgKey(val armored: String) {
    companion object {
        /** Loads an armored key from a string. */
        fun loadArmored(text: String): GpgKey {
            return GpgKey(text)
        }

        /** Dearmors a string and gives back the result. */
        private fun dearmor(text: String): String {
            val process = run(true, "/", "gpg", "--dearmor", "-o-")

            text.lines().forEach { process.inputLine(it) }
            process.closeInput()
            process.use { }

            return process
                .filter { it.stream == Line.IOStream.OUT }
                .joinToString(System.lineSeparator()) { it.text }
        }

        /** Dearmors multiple keys. */
        fun dearmor(vararg keys: GpgKey): String {
            return dearmor(keys.joinToString(System.lineSeparator()) { it.armored })
        }

        /** Dearmors a list of keys together. */
        fun dearmor(keys: List<GpgKey>): String {
            return dearmor(keys.joinToString(System.lineSeparator()) { it.armored })
        }
    }
}
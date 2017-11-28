package uk.co.jrtapsell.fyp.gpgWrapper

import uk.co.jrtapsell.fyp.processTools.Line
import uk.co.jrtapsell.fyp.processTools.run

data class GpgKey(val armored: String) {
    companion object {
        fun loadArmored(text: String): GpgKey {
            return GpgKey(text)
        }

        private fun dearmor(text: String): String {
            val process = run(true, "/", "gpg", "--dearmor", "-o-")

            text.lines().forEach { process.inputLine(it) }
            process.closeInput()
            process.use { }

            return process
                .filter { it.stream == Line.IOStream.OUT }
                .joinToString(System.lineSeparator()) { it.text }
        }

        fun dearmor(vararg keys: GpgKey): String {
            return dearmor(keys.joinToString(System.lineSeparator()) { it.armored })
        }
    }
}
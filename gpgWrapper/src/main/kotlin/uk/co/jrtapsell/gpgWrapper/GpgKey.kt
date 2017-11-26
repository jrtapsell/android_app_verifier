package uk.co.jrtapsell.gpgWrapper

import uk.co.jrtapsell.gitWrapper.processIO.Line
import uk.co.jrtapsell.gitWrapper.processIO.run

class GpgKey(val armored: String) {
    companion object {
        fun loadArmored(text: String): GpgKey {
            return GpgKey(text)
        }

        fun dearmor(text: String): String {
            val process = run(true, "/", "gpg", "--dearmor", "-o-")

            text.lines().forEach { process.inputLine(it) }
            process.closeInput()
            process.use { }

            val unarmored = process
                .filter { it.stream == Line.IOStream.OUT }
                .joinToString(System.lineSeparator()) { it.text }
            return unarmored
        }

        fun dearmor(vararg keys: GpgKey): String {
            return dearmor(keys.joinToString(System.lineSeparator()){it.armored})
        }
    }
}
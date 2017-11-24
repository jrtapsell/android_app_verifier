package uk.co.jrtapsell.jarInjector

class JarManipulator(val filename: String) {
    fun inject(
        outputJarfile: String,
        file: String): JarManipulator {
        throw AssertionError()
    }

    fun sign(outputJarfile: String): JarManipulator {
        val jarCommand = System.getenv("JRT_JARSIGNER_COMMAND") ?:
                throw AssertionError("I don't know how to sign stuff :(")

        val parts = jarCommand.split(" ")
            .map { if (it == "$1") filename else it }
        val process = ProcessBuilder()
            .command(parts)
            .start()
        return this
    }

    fun unsign(
        outputJarfile: String): JarManipulator {
        throw AssertionError()
    }
}
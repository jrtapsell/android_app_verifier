import java.io.File

class JarManipulator(val filename: String) {
    fun inject(
        outputJarfile: String,
        file: String): JarManipulator {
        throw AssertionError()
    }

    fun sign(
        outputJarfile: String): JarManipulator {
        throw AssertionError()
    }

    fun unsign(
        outputJarfile: String): JarManipulator {
        throw AssertionError()
    }
}
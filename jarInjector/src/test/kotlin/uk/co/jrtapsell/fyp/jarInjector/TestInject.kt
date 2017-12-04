package uk.co.jrtapsell.fyp.jarInjector

import org.testng.annotations.Test
import java.io.File

import uk.co.jrtapsell.fyp.baseUtils.testUtils.*

/** Tests injecting into a JAR file. */
class TestInject {
    private fun withTempDir(block: ((String) -> String)->Unit) {
        val tempFile = File.createTempFile("testInject", ".jar")
        tempFile.delete()
        tempFile.mkdirs()
        try {
            block({ tempFile.resolve(it).canonicalPath!! })
        } finally {
            tempFile.deleteRecursively()
        }
    }

    /** Takes an unsigned jar, signs it and then verifies that the resulting jar is treated as signed. */
    @Test
    fun `Signs an unsigned jar`() {
        withTempDir { tempDir ->
            val unsigned = JarManipulator("../demoJars/unsigned.jar")
            unsigned.info.getTotalSigners().isEmpty().assertTrue()
            val signedPath = tempDir("signed.jar")
            val signed = unsigned.sign(signedPath)
            signed.info.getTotalSigners().isNotEmpty().assertTrue("Should be signed")
        }
    }

    /** Takes a signed jar and removes its signature, then validates it is no longer treated as signed. */
    @Test
    fun `Unsigns a signed jar`() {
        withTempDir { tempDir ->
            val signed = JarManipulator("../demoJars/sqljdbc42.jar")
            signed.info.getTotalSigners().isNotEmpty().assertTrue()
            val signedPath = tempDir("unsigned.jar")
            val unsigned = signed.unsign(signedPath)
            unsigned.info.getTotalSigners().isEmpty().assertTrue()
        }
    }

    /** Adds a file to a JAR, and then validates the JAR contains the same files, and the new one aswell. */
    @Test
    fun `Adds a file to a jar`() {
        withTempDir { tempDir ->
            fun JarManipulator.list() = this.info.walkFiles()
                .map { it.path.joinToString("/") }
                .toSet()
                .toMutableSet()

            val base = JarManipulator("../demoJars/unsigned.jar")
            val filenames = base.list()
            val injected = base.inject(
                    tempDir("injected.txt"),
                    "demo.txt",
                    "Hello World")
            val injectedNames = injected.list()
            filenames.add("demo.txt")
            injectedNames assertEquals filenames
        }
    }

    /** Checks that an error is thrown if a set of operations would lead to a broken signature. */
    @Test(expectedExceptions = arrayOf(AssertionError::class))
    fun `Checks signed jars cannot be injected into`() {
        withTempDir { tempDir ->
            val base = JarManipulator("../demoJars/sqljdbc42.jar")
            base.inject(tempDir("unsigned.jar"), "demo", "demo")
        }
    }
}
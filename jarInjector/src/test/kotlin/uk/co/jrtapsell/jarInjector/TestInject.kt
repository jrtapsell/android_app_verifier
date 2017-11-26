package uk.co.jrtapsell.jarInjector

import org.testng.Assert.*
import org.testng.annotations.Test
import java.io.File

class TestInject {
    fun withTempDir(block: ((String) -> String)->Unit) {
        val tempFile = File.createTempFile("testInject", ".jar")
        tempFile.delete()
        tempFile.mkdirs()
        block({ tempFile.resolve(it).canonicalPath!! })
        tempFile.deleteRecursively()
    }

    @Test
    fun `Signs an unsigned jar`() {
        withTempDir { tempDir ->
            val unsigned = JarManipulator("../demoJars/unsigned.jar")
            assertTrue(unsigned.info.getTotalSigners().isEmpty())
            val signedPath = tempDir("signed.jar")
            val signed = unsigned.sign(signedPath)
            assertTrue(signed.info.getTotalSigners().isNotEmpty())
        }
    }

    @Test
    fun `Unsigns a signed jar`() {
        withTempDir { tempDir ->
            val signed = JarManipulator("../demoJars/sqljdbc42.jar")
            assertTrue(signed.info.getTotalSigners().isNotEmpty())
            val signedPath = tempDir("unsigned.jar")
            val unsigned = signed.unsign(signedPath)
            assertTrue(unsigned.info.getTotalSigners().isEmpty())
        }
    }

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
            assertEquals(injectedNames, filenames)
        }
    }
}
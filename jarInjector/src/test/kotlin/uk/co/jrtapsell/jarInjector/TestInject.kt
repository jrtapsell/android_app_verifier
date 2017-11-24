package uk.co.jrtapsell.jarInjector

import org.testng.Assert
import org.testng.annotations.Test
import uk.co.jrtapsell.jarInfo.JarInfo
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class TestInject {
    fun mkTemp(): File {
        val tempFile = File.createTempFile("testInject", ".jar")
        tempFile.delete()
        tempFile.mkdirs()
        return tempFile;
    }

    val JarManipulator.info: JarInfo
        get() = JarInfo(filename)

    @Test(enabled = false)
    fun test() {
        val tempDir = mkTemp()
        try {
            val unsignedPath = tempDir.resolve("unsigned.jar").toPath()
            Files.copy(Paths.get("../demoJars/unsigned.jar"), unsignedPath)
            val unsigned = JarManipulator(unsignedPath.toString())
            Assert.assertFalse(unsigned.info.isSigned())
            val signedPath = tempDir.resolve("signed").canonicalPath!!
            val signed = unsigned.sign(signedPath)
            Assert.assertTrue(signed.info.isSigned())

        } finally {
            tempDir.deleteRecursively()
        }
    }
}
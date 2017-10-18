import org.junit.Assert
import org.junit.Test

class TestJarInfo {
    val info = JarInfo("../demoFiles/old.jar")
    @Test
    fun simpleList() {
        val ret = info.walkFiles().toList()
        Assert.assertNotEquals(0, ret.size)
    }

    @Test
    fun listFiles() {
        val ret = JarInfo("../demoFiles/unsigned.jar").listDirectory("uk","co","jrtapsell","demoJar").toList()
        Assert.assertNotEquals(0, ret.size)
    }

    @Test
    fun checkSig() {
        val ret = info.isSigned()
    }
}

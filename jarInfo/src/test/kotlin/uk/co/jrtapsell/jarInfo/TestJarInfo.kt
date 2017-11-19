package uk.co.jrtapsell.jarInfo

import org.testng.Assert
import org.testng.annotations.Test

class TestJarInfo {
    @Test(dataProviderClass = JarProvider::class, dataProvider = "jars")
    fun simpleList(tj: TestJar) {
        val info = JarInfo(tj.path)
        val ret = info.walkFiles().toList()
        Assert.assertNotEquals(0, ret.size)
    }

    @Test(dataProviderClass = JarProvider::class, dataProvider = "jars")
    fun listFiles(tj: TestJar) {
        val info = JarInfo(tj.path)
        val ret = info.listDirectory("META-INF")
        Assert.assertNotEquals(0, ret.count())
    }

    @Test(dataProviderClass = JarProvider::class, dataProvider = "jars")
    fun checkSig(tj: TestJar) {
        val info = JarInfo(tj.path)
        val ret = info.isSigned()
        Assert.assertEquals(ret, tj.signed)
    }

    @Test(dataProviderClass = JarProvider::class, dataProvider = "jars")
    fun checkValid(tj: TestJar) {
        val info = JarInfo(tj.path)
        val ret = info.isSigned()
        Assert.assertEquals(ret, tj.signed)
    }
}

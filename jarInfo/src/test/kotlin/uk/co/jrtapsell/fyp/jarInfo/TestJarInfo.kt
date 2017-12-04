package uk.co.jrtapsell.fyp.jarInfo

import org.testng.annotations.Test

import uk.co.jrtapsell.fyp.baseUtils.testUtils.*

/** Test the jar info. */
class TestJarInfo {
    /** Checks all of the test jar files, as none should contain 0 files. */
    @Test(dataProviderClass = JarProvider::class, dataProvider = "jars")
    fun `Checks that none of the test jars have 0 files in `(tj: TestJar) {
        val info = JarInfo(tj.path)
        val ret = info.walkFiles().toList()
        ret.size assertNotEquals 0
    }

    /** Checks that META-INF is not empty (should always contain manifest). */
    @Test(dataProviderClass = JarProvider::class, dataProvider = "jars")
    fun `Checks that directory listings work for META-INF `(tj: TestJar) {
        val info = JarInfo(tj.path)
        val ret = info.listDirectory("META-INF").toList()
        ret.count() assertNotEquals 0
    }

    /** Checks that the jars that should be signed are signed.*/
    @Test(dataProviderClass = JarProvider::class, dataProvider = "jars")
    fun `Checks the jars signiture statuses are as expected `(tj: TestJar) {
        val info = JarInfo(tj.path)
        val ret = info.getTotalSigners()
        val shouldBeSigned = tj.signed
        val isSigned = !ret.isEmpty()
        (shouldBeSigned xor isSigned).assertFalse()
    }

    /** Checks for listing of a long empty directory. */
    @Test(dataProviderClass = JarProvider::class, dataProvider = "jars")
    fun `Checks that long empty dirs are empty `(tj: TestJar) {
        val info = JarInfo(tj.path)
        val ret = info.listDirectory("nx/nx/nx/nx/nx").toList()
        ret.count() assertEquals 0
    }

    /** Checks for listing of a short empty directory. */
    @Test(dataProviderClass = JarProvider::class, dataProvider = "jars")
    fun `Checks that short empty dirs are empty `(tj: TestJar) {
        val info = JarInfo(tj.path)
        val ret = info.listDirectory("nx").toList()
        ret.count() assertEquals 0
    }
}

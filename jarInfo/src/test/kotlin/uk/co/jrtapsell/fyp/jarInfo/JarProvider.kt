package uk.co.jrtapsell.fyp.jarInfo

import org.testng.annotations.DataProvider

/** Represents a testing JAR file. */
data class TestJar(val path: String, val signed: Boolean, val signers: List<String>)

/** Modify for each testing environment. */
val TEST_JARS = listOf(
        TestJar("../demoJars/old.jar", false, listOf()),
        TestJar("../demoJars/unsigned.jar", false, listOf()),
        TestJar("../demoJars/sqljdbc42.jar", true, listOf()),
        TestJar("../demoJars/self.jar", false, listOf()),
        TestJar("../demoJars/web.jar", false, listOf())
)

/** A data provider for the testing JAR files. */
class JarProvider {
    /** Gets all of the test JARS. */
    @DataProvider(name = "jars")
    fun getJars(): Array<Array<TestJar>> {
        return TEST_JARS.map { arrayOf(it) }.toTypedArray()
    }
}
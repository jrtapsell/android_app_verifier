package uk.co.jrtapsell.fyp.jarInfo

import org.testng.annotations.DataProvider

data class TestJar(val path: String, val signed: Boolean, val signers: List<String>)

/** Modify for each testing environment. */
val TEST_REPOS = listOf(
        TestJar("../demoJars/old.jar", false, listOf()),
        TestJar("../demoJars/unsigned.jar", false, listOf()),
        TestJar("../demoJars/sqljdbc42.jar", true, listOf()),
        TestJar("../demoJars/self.jar", false, listOf()),
        TestJar("../demoJars/web.jar", false, listOf())
)

class JarProvider {
    @DataProvider(name = "jars")
    fun getRepos(): Array<Array<TestJar>> {
        return TEST_REPOS.map { arrayOf(it) }.toTypedArray()
    }
}
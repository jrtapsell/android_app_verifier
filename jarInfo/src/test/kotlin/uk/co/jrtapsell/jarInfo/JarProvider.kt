package uk.co.jrtapsell.jarInfo

import org.testng.annotations.DataProvider

data class TestJar(val path: String, val signed: Boolean, val signers: List<String>)

/** Modify for each testing environment. */
val TEST_REPOS = listOf(
        TestJar("demoFiles/old.jar", false, listOf()),
        TestJar("demoFiles/unsigned.jar", false, listOf()),
        TestJar("demoFiles/sqljdbc42.jar", true, listOf()),
        TestJar("demoFiles/self.jar", false, listOf()),
        TestJar("demoFiles/web.jar", false, listOf())
)

class JarProvider {
    @DataProvider(name = "jars")
    fun getRepos(): Array<Array<TestJar>> {
        return TEST_REPOS.map { arrayOf(it) }.toTypedArray()
    }
}
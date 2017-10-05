package uk.co.jrtapsell.gitWrapper

import org.testng.annotations.DataProvider
import org.testng.annotations.Test

/**
 * @author James Tapsell
 */
class TestGit {

    @DataProvider(name = "repos")
    fun listRepos(): Array<Array<String>> = arrayOf(
            "/home/james/FYP/demoRepo",
            "/home/james/FYP/android_app_verifier"
    ).map { arrayOf(it) }.toTypedArray()

    @Test(dataProvider = "repos")
    fun makeGit(path: String) {
        var repository = Git(path)
    }
}
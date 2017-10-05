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
            PROJECT_DIR
    ).map { arrayOf(it) }.toTypedArray()

    @Test(dataProvider = "repos")
    fun `Constructs a repo for a known path `(path: String) {
        var repository = Git(path)
    }
}
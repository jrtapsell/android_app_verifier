package uk.co.jrtapsell.gitWrapper

import org.testng.Assert
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
        Git(path)
    }

    @Test(dataProvider = "repos")
    fun `Lists commits for the given repo `(path: String) {
        val repository = Git(path)
        val commits = repository.listCommits()
        Assert.assertNotEquals(commits.size, 0)
        for (commit in commits) {
            println(commit)
        }
    }
}
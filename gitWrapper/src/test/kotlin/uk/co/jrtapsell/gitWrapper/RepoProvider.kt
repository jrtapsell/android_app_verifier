package uk.co.jrtapsell.gitWrapper

import org.testng.annotations.DataProvider
import uk.co.jrtapsell.gitWrapper.data.SignatureStatus

data class Repo(val path: String, val state: SignatureStatus)

/** Modify for each testing environment. */
val TEST_REPOS = listOf(
        Repo("./", SignatureStatus.GOOD),
        Repo("/home/james/binBot", SignatureStatus.UNSIGNED)
)

class RepoProvider {
    @DataProvider(name = "repos")
    fun getRepos(): Array<Array<Repo>> {
        return TEST_REPOS .map { arrayOf(it) }.toTypedArray()
    }
}
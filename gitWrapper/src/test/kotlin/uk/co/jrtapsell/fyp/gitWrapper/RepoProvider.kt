package uk.co.jrtapsell.fyp.gitWrapper

import org.testng.annotations.DataProvider
import uk.co.jrtapsell.fyp.gitWrapper.data.SignatureStatus

data class Repo(val path: String, val state: SignatureStatus)

/** Modify for each testing environment. */
val TEST_REPOS = listOf(
        Repo("../gitRepos/android_app_verifier", SignatureStatus.GOOD),
        Repo("../gitRepos/signedRepo", SignatureStatus.GOOD),
        Repo("../gitRepos/wordpressSitesListing", SignatureStatus.GOOD),
        Repo("../gitRepos/personal", SignatureStatus.UNSIGNED)
)

class RepoProvider {
    @DataProvider(name = "repos")
    fun getRepos(): Array<Array<Repo>> {
        return TEST_REPOS.map { arrayOf(it) }.toTypedArray()
    }
}
package uk.co.jrtapsell.fyp.gitWrapper

import org.testng.annotations.DataProvider
import uk.co.jrtapsell.fyp.gitWrapper.data.SignatureStatus

/** Represents a test repository. */
data class Repo(val path: String, val state: SignatureStatus)

/** Modify for each testing environment. */
val TEST_REPOS = listOf(
        Repo("../gitRepos/android_app_verifier", SignatureStatus.GOOD),
        Repo("../gitRepos/signedRepo", SignatureStatus.GOOD),
        Repo("../gitRepos/wordpressSitesListing", SignatureStatus.GOOD),
        Repo("../gitRepos/personal", SignatureStatus.UNSIGNED)
)

/** Allows tests to run over all of the test repos. */
class RepoProvider {
    /** Runs the test against each of the test repos. */
    @DataProvider(name = "repos")
    fun getRepos(): Array<Array<Repo>> {
        return TEST_REPOS.map { arrayOf(it) }.toTypedArray()
    }
}
package uk.co.jrtapsell.fyp.gitWrapper.data

import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * @author James Tapsell
 */
data class Commit(
    val commitHash: Hash,
    private val parentHashes: List<Hash>,
    private val subject: String,
    private val author: Identity,
    private val committer: Identity,
    val signer: SignedIdentity?) {

    companion object {
        private fun convert(
                commitHash: String,
                parentHashes: String,
                subject: String,
                authorName: String,
                authorEmail: String,
                committerName: String,
                committerEmail: String,
                statusCode: String,
                gpgSigner: String,
                gpgKey: String): Commit {
            var author = Identity(authorName, authorEmail, null)
            var committer = Identity(committerName, committerEmail, null)

            val status = SignatureStatus.getForLetter(statusCode[0])
            var signIdent: SignedIdentity? = null

            if (!gpgSigner.isBlank()) {
                val unsignedGpg = Identity.fromGpgString(gpgSigner)
                signIdent = SignedIdentity(unsignedGpg, unsignedGpg.comment, status, gpgKey)
                if (author.email == signIdent.email) {
                    author = SignedIdentity(author, signIdent.comment, status, gpgKey)
                }
                if (committer.email == signIdent.email) {
                    committer = SignedIdentity(committer, signIdent.comment, status, gpgKey)
                }
            }
            
            val parents = parentHashes.split(" ")
                    .filter { it.isNotEmpty() }
                    .map { Hash.fromString(it)!! }

            return Commit(
                    Hash.fromString(commitHash)!!,
                    parents,
                    subject,
                    author,
                    committer,
                    signIdent)
        }
        val PRETTY_STRING = "{" +
                "commitHash=\"%H\"," +
                "parentHashes=\"%P\"," +
                "subject=\"%s\"," +
                "authorName=\"%an\"," +
                "authorEmail=\"%ae\"," +
                "committerEmail=\"%ce\"," +
                "committerName=\"%cn\"," +
                "gpgSigner=\"%GS\"," +
                "gpgKey=\"%GK\"," +
                "status=\"%G?\"" +
                "}"

        fun convert(raw: String): Commit {
            val g = Gson().fromJson(raw, JsonObject::class.java)
            return convert(
                    g["commitHash"].asString,
                    g["parentHashes"].asString,
                    g["subject"].asString,
                    g["authorName"].asString,
                    g["authorEmail"].asString,
                    g["committerName"].asString,
                    g["committerEmail"].asString,
                    g["status"].asString,
                    g["gpgSigner"].asString,
                    g["gpgKey"].asString
            )
        }
    }

    override fun toString() = """
        | ┏━━━━━━━━━━━━━┯━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        | ┃ Hash        │ $commitHash
        | ┃ Parent      │ $parentHashes
        | ┃ Subject     │ "$subject"
        | ┃ Author      │ $author
        | ┃ Committer   │ $committer
        | ┃ Signer      │ $signer
        | ┗━━━━━━━━━━━━━┷━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    """.trimMargin()
}
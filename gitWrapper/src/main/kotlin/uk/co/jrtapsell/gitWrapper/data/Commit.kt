package uk.co.jrtapsell.gitWrapper.data

import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * @author James Tapsell
 */
data class Commit(
        val commitHash: String,
        val parentHashes: String,
        val subject: String,
        val author: Identity,
        val committer: Identity,
        val status: String,
        val gpgSigner: String,
        val gpgKey: String) {

    companion object {
        fun convert(
            commitHash: String,
            parentHashes: String,
            subject: String,
            authorName: String,
            authorEmail: String,
            committerName: String,
            committerEmail: String,
            status: String,
            gpgSigner: String,
            gpgKey: String): Commit {
            var author = Identity(authorName, authorEmail, null)
            var committer = Identity(committerName, committerEmail, null)

            if (!gpgSigner.isBlank()) {
                val signIdent = Identity.fromGpgString(gpgSigner)
                if (author.email == signIdent.email) {
                    author = SignedIdentity(author, signIdent.comment!!, status, gpgKey)
                }
                if (committer.email == signIdent.email) {
                    committer = SignedIdentity(committer, signIdent.comment!!, status, gpgKey)
                }
            }
            return Commit(
                    commitHash,
                    parentHashes,
                    subject,
                    author,
                    committer,
                    status,
                    gpgSigner,
                    gpgKey)
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
        | ┃ Status      │ $status
        | ┃ GPG Key     │ $gpgKey
        | ┃ GPG Signer  │ $gpgSigner
        | ┗━━━━━━━━━━━━━┷━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    """.trimMargin()
}
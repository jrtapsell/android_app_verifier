package uk.co.jrtapsell.gitWrapper.data

open class Identity(
    val realName: String,
    val email: String,
    val comment: String?) {
    override fun toString() = "$realName ($comment) <$email>"
}

class SignedIdentity(
        realName: String,
        email: String,
        comment: String?,
        val status: Char,
        val gpgName: String,
        val gpgKey: String
) : Identity(realName, email, comment)
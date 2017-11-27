package uk.co.jrtapsell.fyp.gitWrapper.data

open class Identity(
    val realName: String,
    val email: String,
    val comment: String?) {
    override fun toString() = "$realName ($comment) <$email>"

    companion object {
        private val GPG_REGEX = Regex("""([^(]+?) *\(([^)]+)\) *<([^>]+)>""")
        fun fromGpgString(gpgString: String): Identity {
            val v = GPG_REGEX.matchEntire(gpgString)!!
            val real = v.groups[0]!!.value
            val comment = v.groups[1]!!.value
            val email = v.groups[2]!!.value
            return Identity(real, email, comment)
        }
    }
}

class SignedIdentity(
    base: Identity,
    comment: String?,
    val status: SignatureStatus,
    val gpgKey: String) : Identity(base.realName, base.email, comment) {
    override fun toString(): String = "${super.toString()} signed [$status] by [$gpgKey]"
}
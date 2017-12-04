package uk.co.jrtapsell.fyp.gitWrapper.data

/** Represents a type of git identity. */
open class Identity(
    val realName: String,
    val email: String,
    val comment: String?) {
    /** Gets the string representation. */
    override fun toString() = "$realName ($comment) <$email>"

    companion object {
        private val GPG_REGEX = Regex("""([^(]+?) *\(([^)]+)\) *<([^>]+)>""")
        /** Converts a string to its Identity representation. */
        fun fromGpgString(gpgString: String): Identity {
            val v = GPG_REGEX.matchEntire(gpgString)!!
            val real = v.groups[0]!!.value
            val comment = v.groups[1]!!.value
            val email = v.groups[2]!!.value
            return Identity(real, email, comment)
        }
    }
}

/** A GPG signed identity. */
class SignedIdentity(
    base: Identity,
    comment: String?,
    val status: SignatureStatus,
    private val gpgKey: String) : Identity(base.realName, base.email, comment) {
    /** Represents this identity as a string. */
    override fun toString(): String = "${super.toString()} signed [$status] by [$gpgKey]"
}
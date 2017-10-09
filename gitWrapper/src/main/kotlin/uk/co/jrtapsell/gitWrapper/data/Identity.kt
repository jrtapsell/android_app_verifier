package uk.co.jrtapsell.gitWrapper.data

open class Identity(
    val realName: String,
    val email: String,
    val comment: String?) {
    override fun toString() = "$realName ($comment) <$email>"

    companion object {
        private val GPG_REGEX = Regex("""(?<real>[^(]+?) *\((?<comment>[^)]+)\) *<(?<email>[^>]+)>""")
        fun fromGpgString(gpgString: String): Identity {
            val v = GPG_REGEX.matchEntire(gpgString)!!
            val real = v.groups["real"]!!.value
            val comment = v.groups["comment"]!!.value
            val email = v.groups["email"]!!.value
            return Identity(real, email, comment)
        }
    }
}

class SignedIdentity(
        base: Identity,
        comment: String,
        val status: String,
        val gpgKey: String
) : Identity(base.realName, base.email, comment) {
    override fun toString(): String = "${super.toString()} signed [$status] by [$gpgKey]"
}
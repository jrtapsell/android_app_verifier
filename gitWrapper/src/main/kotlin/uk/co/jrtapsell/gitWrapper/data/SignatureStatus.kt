package uk.co.jrtapsell.gitWrapper.data

/** Represents a commit signature status. */
enum class SignatureStatus(val code: Char) {

    /**
     * A good signature.
     *
     * **Documentation:** _for a good (valid) signature_
     */
    GOOD('G'),

    /**
     * A bad signature (a signature that has been modified or messed with).
     *
     * **Documentation:** _for a bad signature_
     */
    BAD('B'),

    /**
     * A good signature with an unknown key, so the owner cannot be identified.
     *
     * **Documentation:** _for a good signature with unknown validity_
     */
    UNKNOWN_KEY('U'),

    /**
     * A signature with a key that was valid before the signature, but became invalid since.
     *
     * **Documentation:** _for a good signature that has expired_
     */
    EXPIRED_AFTER_SIGNATURE('X'),

    /**
     * A signature with a key that was invalid at the time of signature.
     *
     * **Documentation:** _for a good signature made by an expired key_
     */
    EXPIRED_BEFORE_SIGNATURE('Y'),

    /**
     * Signed with a revoked key.
     *
     * **Documentation:** _for a good signature made by a revoked key_
     */
    REVOKED('R'),

    /**
     * Signed with a revoked key.
     *
     * **Documentation:** _if the signature cannot be checked (e.g. missing key)_
     */
    UNCHECKABLE('E'),

    /**
     * Not signed.
     *
     * **Documentation:** _for no signature_
     */
    UNSIGNED('N');
    
    companion object {
        private val CODES = values().associate { it.code to it }
        fun getForLetter(code: Char): SignatureStatus {
            return CODES[code] ?: throw AssertionError("Unknown code: $code")
        }
    }
}
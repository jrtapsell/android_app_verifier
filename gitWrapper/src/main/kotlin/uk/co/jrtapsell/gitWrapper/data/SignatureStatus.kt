package uk.co.jrtapsell.gitWrapper.data

/**
 * Represents a commit signature status, the documentation lists the following values:
 *
 *   - "G" for a good (valid) signature,
 *   - "B" for a bad signature,
 *   - "U" for a good signature with unknown validity,
 *   - "X" for a good signature that has expired,
 *   - "Y" for a good signature made by an expired key,
 *   - "R" for a good signature made by a revoked key,
 *   - "E" if the signature cannot be checked (e.g. missing key) and
 *   - "N" for no signature
 */
enum class SignatureStatus(val code: Char) {
    GOOD('G'),
    BAD('B'),
    UNKNOWN_KEY('U'),
    EXPIRED_AFTER_SIGNATURE('X'),
    EXPIRED_BEFORE_SIGNATURE('Y'),
    REVOKED('R'),
    UNCHECKABLE('E'),
    UNSIGNED('N');
    
    companion object {
        val CODES = values().associate { it.code to it }
        fun getForLetter(code: Char): SignatureStatus {
            return CODES[code] ?: throw AssertionError("Unknown code: $code")
        }
    }
}
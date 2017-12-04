package uk.co.jrtapsell.fyp.keybaseConnector

/** The types of verification that Keybase supports. */
enum class VerificationType(val queryName: String) {
    /** Keybase usernames. */
    KEYBASE("username"),
    /** GitHub accounts. */
    GITHUB("github"),
    /** Websites via DNS records. */
    DNS("dns"),
    /** Websites via a file hosted over HTTPS. */
    HTTPS("https"),
    /** PGP keys. */
    PGP("key_fingerprint")
}
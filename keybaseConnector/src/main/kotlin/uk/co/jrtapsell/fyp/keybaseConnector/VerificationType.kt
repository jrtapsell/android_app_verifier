package uk.co.jrtapsell.fyp.keybaseConnector

enum class VerificationType(val queryName: String) {
    KEYBASE("username"),
    GITHUB("github"),
    DNS("dns"),
    HTTPS("https"),
    PGP("key_fingerprint")
}
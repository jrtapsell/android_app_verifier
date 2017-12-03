package uk.co.jrtapsell.fyp.keybaseConnector.dataclasses

/**
 * Represents a list of data about 0 or more users.
 *
 * A wrapper is used so that KeyBaseMapper can detect when it should return a list of users.
 */
data class UsersData(private val payload: List<UserData>): List<UserData> by payload {
    companion object {
        /** Constant for when no users are found. */
        val NO_USERS = UsersData(listOf())
    }
}
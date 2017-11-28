package uk.co.jrtapsell.fyp.keybaseConnector.dataclasses

data class UsersData(private val payload: List<UserData>): List<UserData> by payload
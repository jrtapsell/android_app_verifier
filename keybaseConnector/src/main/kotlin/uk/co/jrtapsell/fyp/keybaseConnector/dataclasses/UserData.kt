package uk.co.jrtapsell.fyp.keybaseConnector.dataclasses

import org.json.JSONObject
import uk.co.jrtapsell.fyp.keybaseConnector.KeybaseException
import uk.co.jrtapsell.fyp.keybaseConnector.KeybaseVerifier

/** Represents a Keybase user. */
data class UserData(val id: String, val usernames: Map<String, String>, val username: String) {
    companion object {
        /** Creates a user from their JSON representation. */
        fun create(them: JSONObject): UserData? {
            val username = them.getJSONObject("basics")?.getString("username_cased") ?:
                    throw KeybaseException("Bad data format: $them")
            val usenames = mutableMapOf(
                    "keybase" to username
            )
            them.getJSONObject("proofs_summary").getJSONArray("all").forEach {
                it as JSONObject
                usenames.put(it.getString("proof_type"), it.getString("nametag"))
            }
            return UserData(
                    them.getString("id"),
                    usenames,
                    username
            )
        }
    }

    /** Gets the signature verifier for this user. */
    fun getVerifier() = KeybaseVerifier(username)

}
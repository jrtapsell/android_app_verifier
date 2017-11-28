package uk.co.jrtapsell.fyp.keybaseConnector.dataclasses

import org.json.JSONObject
import uk.co.jrtapsell.fyp.gpgWrapper.GpgKey

data class UserData(val id: String, val usernames: Map<String, String>, val keys: List<GpgKey>) {
    companion object {
        fun create(them: JSONObject): UserData? {
            val usenames = mutableMapOf(
                    "keybaseConnector" to them.getJSONObject("basics").getString("username_cased")!!
            )
            them.getJSONObject("proofs_summary").getJSONArray("all").forEach {
                it as JSONObject
                usenames.put(it.getString("proof_type"), it.getString("nametag"))
            }
            val keys = them.getJSONObject("public_keys").getJSONArray("all_bundles")
                .map { it as String }
            .filter {
                it.startsWith("-----BEGIN PGP PUBLIC KEY BLOCK-----")
            }.map {
                GpgKey.loadArmored(it)
            }
            return UserData(
                    them.getString("id"),
                    usenames,
                    keys
            )
        }
    }
}
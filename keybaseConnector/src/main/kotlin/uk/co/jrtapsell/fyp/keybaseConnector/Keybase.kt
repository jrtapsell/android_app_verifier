package uk.co.jrtapsell.fyp.keybaseConnector

import com.mashape.unirest.http.Unirest
import uk.co.jrtapsell.fyp.keybaseConnector.dataclasses.KeybaseMapper
import uk.co.jrtapsell.fyp.keybaseConnector.dataclasses.UserData

object Keybase {

    private val baseURL = "https://keybase.io/_/api/1.0/user/lookup.json"
    private val fields = "basics,public_keys,proofs_summary,profile"

    init {
        Unirest.setObjectMapper(KeybaseMapper)
    }

    fun getByUsername(keybaseUsername: String): UserData? {
        return Unirest.get(baseURL)
            .queryString("username", keybaseUsername)
            .queryString("fields", fields)
            .asObject(UserData::class.java)
            .body
    }
}
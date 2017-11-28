package uk.co.jrtapsell.fyp.keybaseConnector

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.request.GetRequest
import com.mashape.unirest.request.HttpRequest
import uk.co.jrtapsell.fyp.keybaseConnector.dataclasses.KeybaseMapper
import uk.co.jrtapsell.fyp.keybaseConnector.dataclasses.UserData
import uk.co.jrtapsell.fyp.keybaseConnector.dataclasses.UsersData

object Keybase {

    private val baseURL = "https://keybase.io/_/api/1.0/user/lookup.json"
    private val fields = "basics,public_keys,proofs_summary,profile"

    init {
        Unirest.setObjectMapper(KeybaseMapper)
    }

    private inline fun <reified T> get(block:HttpRequest.() -> HttpRequest): T? {
        return Unirest.get(baseURL)
            .block()
            .queryString("fields", fields)
            .asObject(T::class.java)
            .body
    }

    fun getByUsername(keybaseUsername: String): UserData? {
        return get{
            queryString("username", keybaseUsername)
        }
    }

    fun getByGitHub(github: String): UsersData? {
        return get{
            queryString("github", github)
        }
    }

    fun getByDomain(domain: String): UsersData? {
        val dns = get {
            queryString("dns", domain)
        }?: UsersData(listOf())
        val http = get {
            queryString("https", domain)
        }?: UsersData(listOf())
        return UsersData(dns + http)
    }
}
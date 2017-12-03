package uk.co.jrtapsell.fyp.keybaseConnector

import com.mashape.unirest.http.Unirest
import uk.co.jrtapsell.fyp.keybaseConnector.dataclasses.KeybaseMapper
import uk.co.jrtapsell.fyp.keybaseConnector.dataclasses.UserData
import uk.co.jrtapsell.fyp.keybaseConnector.dataclasses.UsersData

import uk.co.jrtapsell.fyp.keybaseConnector.VerificationType.*

object Keybase {

    private val baseURL = "https://keybase.io/_/api/1.0/user/lookup.json"
    private val fields = "basics,proofs_summary,profile"

    init {
        Unirest.setObjectMapper(KeybaseMapper)
    }

    private inline fun <reified T> get(type: VerificationType, value: String): T? {
        try {
            return Unirest.get(baseURL)
                .queryString(type.queryName, value)
                .queryString("fields", fields)
                .asObject(T::class.java)
                .body
        } catch (ex: Throwable) {
            KeybaseException.rethrow(ex)
        }
    }

    /** Method to get the user(s) who have verified a particular github username.
     *
     * An example usage would look like:
     *
     * ```getByGitHub("jrtapsell")```
     */
    fun getByUsername(keybaseUsername: String): UserData? {
        return get(KEYBASE, keybaseUsername)
    }


    /** Method to get the user(s) who have verified a particular github username.
     *
     * An example usage would look like:
     *
     * ```getByGitHub("jrtapsell")```
     */
    fun getByGitHub(github: String): UsersData? {
        return get(GITHUB, github)
    }

    /** Method to get the user(s) who have verified a particular domain.
     *
     * An example usage would look like:
     *
     * ```getByDomain("jrtapsell.co.uk")```
     */
    fun getByDomain(domain: String): UsersData? {
        // While it would be nice to be able to get users by one of many attributes,
        // keybase only supports this in a single request.
        val dns = get(DNS, domain)?: UsersData.NO_USERS
        val http = get(HTTPS, domain) ?: UsersData.NO_USERS
        return UsersData(dns + http)
    }
}
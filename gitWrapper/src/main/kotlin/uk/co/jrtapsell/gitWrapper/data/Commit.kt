package uk.co.jrtapsell.gitWrapper.data

import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * @author James Tapsell
 */
data class Commit(val subject: String) {
    companion object {
        val PRETTY_STRING = "{subject=\"%s\"}"

        fun convert(raw: String): Commit {
            val g = Gson().fromJson(raw, JsonObject::class.java)
            return Commit(g["subject"].asString)
        }
    }
}
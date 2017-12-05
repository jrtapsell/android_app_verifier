package uk.co.jrtapsell.fyp.keybaseConnector.dataclasses

import com.mashape.unirest.http.ObjectMapper
import org.jetbrains.annotations.Contract
import org.json.JSONArray
import uk.co.jrtapsell.fyp.keybaseConnector.extensions.isA
import org.json.JSONObject
import uk.co.jrtapsell.fyp.keybaseConnector.KeybaseException

/** Allows Unirest to return my objects instead of JSONObjects. */
object KeybaseMapper: ObjectMapper {
    /** Writes a value (always fails). */
    @Contract("-> fail")
    override fun writeValue(value: Any?) = throw AssertionError()

    @Suppress("UNCHECKED_CAST")
    private inline fun <T> Any.unsafeCast(type: Class<T>) = this as T

    /** Reads an object from a string. */
    override fun <T : Any> readValue(value: String?, valueType: Class<T>): T? {
        if (value == null) return null
        val data = JSONObject(value)
        if (!data.has("status")) throw KeybaseException("Bad API response: $value")
        val status = data.getJSONObject("status")
        checkNotNull(status)
        if (status.getInt("code") == 205 && status.getString("name") == "NOT_FOUND") {
            return null
        }
        KeybaseException.validateResponse(data)
        return when {
            valueType isA UserData::class -> {
                if (!data.has("them")) return null
                val them = data.getJSONObject("them") ?: return null
                UserData.create(them)
            }
            valueType isA UsersData::class -> {
                if (!data.has("them")) return null
                val them = data.getJSONArray("them") ?: return null
                UsersData(them.mapNotNull { UserData.create(it as JSONObject) })
            }
            else -> throw AssertionError("Unknown type: ${valueType.canonicalName}")
        }?.unsafeCast(valueType)
    }
}
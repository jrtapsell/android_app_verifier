package uk.co.jrtapsell.fyp.keybaseConnector.dataclasses

import com.mashape.unirest.http.ObjectMapper
import org.json.JSONArray
import uk.co.jrtapsell.fyp.keybaseConnector.extensions.isA
import org.json.JSONObject

object KeybaseMapper: ObjectMapper {
    override fun writeValue(value: Any?) = throw AssertionError()

    @Suppress("UNCHECKED_CAST")
    private fun <T> Any.unsafeCast(type: Class<T>) = this as T

    override fun <T : Any> readValue(value: String?, valueType: Class<T>): T? {
        if (value == null) return null
        val data = JSONObject(value)
        return when {
            valueType isA UserData::class -> {
                if (!data.has("them")) return null
                val them = data.get("them") as? JSONObject ?: return null
                UserData.create(them)
            }
            valueType isA UsersData::class -> {
                if (!data.has("them")) return null
                val them = data.get("them") as? JSONArray ?: return null
                UsersData(them.mapNotNull { UserData.create(it as JSONObject) })
            }
            else -> throw AssertionError("Unknown type: ${valueType.canonicalName}")
        }?.unsafeCast(valueType)
    }
}
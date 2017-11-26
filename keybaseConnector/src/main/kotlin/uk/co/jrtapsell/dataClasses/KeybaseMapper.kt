package uk.co.jrtapsell.dataClasses

import com.mashape.unirest.http.ObjectMapper
import extensions.isA
import org.json.JSONObject

object KeybaseMapper: ObjectMapper {
    override fun writeValue(value: Any?) = throw AssertionError()

    override fun <T : Any> readValue(value: String?, valueType: Class<T>): T? {
        if (value == null) return null
        val data = JSONObject(value)
        when {
            valueType isA UserData::class -> return UserData.create(data) as? T
            else -> throw AssertionError("Unknown type: ${valueType.canonicalName}")
        }
    }
}
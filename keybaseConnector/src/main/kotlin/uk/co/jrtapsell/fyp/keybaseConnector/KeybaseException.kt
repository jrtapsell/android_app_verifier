package uk.co.jrtapsell.fyp.keybaseConnector

import org.json.JSONObject
import kotlin.reflect.KClass

abstract class KeybaseException(message: String? = null, throwable: Throwable? = null):
        RuntimeException(message, throwable) {
    companion object {

        fun rethrow(throwable: Throwable): Nothing {
            val keybase = transativeCause(throwable, KeybaseException::class)
            throw keybase?: WrappedKeybaseException(throwable)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T: Any> transativeCause(throwable: Throwable, kClass: KClass<T>): T? {
            if (kClass.isInstance(throwable)) {
                return throwable as T
            }
            return throwable.cause?.let { transativeCause(it, kClass) }
        }

        fun validateResponse(data: JSONObject) {
            val status = data.getJSONObject("status")
            if (status.getInt("code") != 0 || status.getString("name") != "OK") {
                throw StatusKeybaseException(
                        status.getInt("code"),
                        status.getString("desc"),
                        status.getString("name")
                )
            }
        }
    }

    class WrappedKeybaseException(throwable: Throwable): KeybaseException(throwable.localizedMessage, throwable)

    data class StatusKeybaseException(
        val code: Int,
        val desctiption: String,
        val name: String
    ): KeybaseException(desctiption, null)
}
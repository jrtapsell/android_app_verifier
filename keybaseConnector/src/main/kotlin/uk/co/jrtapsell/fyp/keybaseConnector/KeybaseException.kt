package uk.co.jrtapsell.fyp.keybaseConnector

import org.json.JSONObject
import kotlin.reflect.KClass

/** Exception for when something goes wrong while using Keybase. */
open class KeybaseException(message: String? = null, throwable: Throwable? = null):
        RuntimeException(message, throwable) {
    companion object {

        /** Rethrows the exception as a Keybase exception. */
        fun rethrow(throwable: Throwable): Nothing {
            val keybase = transativeCause(throwable, KeybaseException::class)
            throw keybase?: WrappedKeybaseException(throwable)
        }

        /** Checks if the given exception or any of its chain of causes are of the given type. */
        @Suppress("UNCHECKED_CAST")
        fun <T: Any> transativeCause(throwable: Throwable, kClass: KClass<T>): T? {
            if (kClass.isInstance(throwable)) {
                return throwable as T
            }
            return throwable.cause?.let { transativeCause(it, kClass) }
        }

        /** Checks if the given response has an error. */
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

    /** Wraps a throwable to make it an instance of KeybaseException. */
    class WrappedKeybaseException(throwable: Throwable): KeybaseException(throwable.localizedMessage, throwable)

    /** Represents an exception, with fields for all of the fields returned from the Keybase API. */
    data class StatusKeybaseException(
        val code: Int,
        val desctiption: String,
        val name: String
    ): KeybaseException(desctiption, null)
}
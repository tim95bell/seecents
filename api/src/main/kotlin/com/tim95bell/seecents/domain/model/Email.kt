package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*
import javax.mail.internet.InternetAddress

@JvmInline
value class Email private constructor(val value: String) {
    sealed interface Error {
        object Invalid : Error
    }

    companion object {
        fun fromInput(raw: String): Result<Error, Email> {
            return create(normalise(raw))
        }

        fun fromCanonical(raw: String): Result<Error, Email> {
            if (raw != normalise(raw)) {
                return error(Error.Invalid)
            }

            return create(raw)
        }

        private fun normalise(raw: String) = raw.trim().lowercase()

        private fun create(canonical: String): Result<Error, Email> {
            return try {
                InternetAddress(canonical).validate()
                ok(Email(canonical))
            } catch (e: Exception) {
                error(Error.Invalid)
            }
        }
    }
}

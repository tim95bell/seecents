package com.tim95bell.seecents.domain.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import jakarta.mail.internet.InternetAddress

@JvmInline
value class Email private constructor(val value: String) {
    sealed interface Error {
        object Invalid : Error
    }

    companion object {
        fun fromInput(raw: String): Either<Error, Email> {
            return create(normalise(raw))
        }

        fun fromCanonical(raw: String): Either<Error, Email> {
            if (raw != normalise(raw)) {
                return Error.Invalid.left()
            }

            return create(raw)
        }

        private fun normalise(raw: String) = raw.trim().lowercase()

        private fun create(canonical: String): Either<Error, Email> {
            return try {
                InternetAddress(canonical).validate()
                Email(canonical).right()
            } catch (_: Exception) {
                Error.Invalid.left()
            }
        }
    }
}

package com.tim95bell.seecents.domain.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right

@JvmInline
value class UserName private constructor(val value: String) {
    sealed interface Error {
        object Invalid : Error
    }

    companion object {
        fun fromInput(raw: String): Either<Error, UserName> {
            val trimmed = raw.trim()
            if (trimmed.isBlank()) {
                return Error.Invalid.left()
            }

            return UserName(trimmed).right()
        }

        fun fromCanonical(raw: String): Either<Error, UserName> {
            return fromInput(raw)
                .flatMap {
                    if (it.value != raw) {
                        Error.Invalid.left()
                    } else {
                        it.right()
                    }
                }
        }
    }
}

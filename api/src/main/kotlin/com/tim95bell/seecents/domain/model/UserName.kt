package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*

@JvmInline
value class UserName private constructor(val value: String) {
    sealed interface Error {
        object Invalid : Error
    }

    companion object {
        fun fromInput(raw: String): Result<Error, UserName> {
            val trimmed = raw.trim()
            if (trimmed.isBlank()) {
                return error(Error.Invalid)
            }

            return ok(UserName(trimmed))
        }

        fun fromCanonical(raw: String): Result<Error, UserName> {
            return fromInput(raw)
                .flatMap {
                    if (it.value != raw) {
                        error(Error.Invalid)
                    } else {
                        ok(it)
                    }
                }
        }
    }
}

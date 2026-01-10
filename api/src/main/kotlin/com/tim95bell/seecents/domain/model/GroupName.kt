package com.tim95bell.seecents.domain.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right

@JvmInline
value class GroupName private constructor(val value: String) {
    sealed interface Error {
        object Invalid : Error
    }

    companion object {
        fun fromInput(raw: String): Either<Error, GroupName> {
            return raw.trim().let {
                if (it.isBlank()) {
                    Error.Invalid.left()
                } else {
                    GroupName(it).right()
                }
            }
        }

        fun fromCanonical(raw: String): Either<Error, GroupName> {
            return fromInput(raw).flatMap {
                if (it.value == raw) {
                    it.right()
                } else {
                    Error.Invalid.left()
                }
            }
        }
    }
}

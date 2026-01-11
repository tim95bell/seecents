package com.tim95bell.seecents.domain.model

import java.util.UUID

@JvmInline
value class UserId(val value: UUID) {
    companion object {
        fun new(): UserId = UserId(UUID.randomUUID())
    }
}

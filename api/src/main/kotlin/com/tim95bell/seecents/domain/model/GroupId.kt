package com.tim95bell.seecents.domain.model

import java.util.UUID

@JvmInline
value class GroupId(val value: UUID) {
    companion object {
        fun new(): GroupId = GroupId(UUID.randomUUID())
    }
}

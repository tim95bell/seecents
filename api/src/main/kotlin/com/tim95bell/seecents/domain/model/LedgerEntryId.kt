package com.tim95bell.seecents.domain.model

import java.util.UUID

@JvmInline
value class LedgerEntryId(val value: UUID) {
    companion object {
        fun new(): LedgerEntryId = LedgerEntryId(UUID.randomUUID())
    }
}

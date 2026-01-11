package com.tim95bell.seecents.domain.model

import java.util.UUID

@JvmInline
value class LedgerEntryLineId(val value: UUID) {
    companion object {
        fun new(): LedgerEntryLineId = LedgerEntryLineId(UUID.randomUUID())
    }
}

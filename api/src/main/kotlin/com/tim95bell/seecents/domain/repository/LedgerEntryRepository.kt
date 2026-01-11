package com.tim95bell.seecents.domain.repository

import com.tim95bell.seecents.domain.model.LedgerEntry

interface LedgerEntryRepository {
    fun save(newEntry: LedgerEntry): LedgerEntry
}

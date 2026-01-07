package com.tim95bell.seecents.domain.repository

import com.tim95bell.seecents.domain.model.LedgerEntry
import com.tim95bell.seecents.domain.model.LedgerEntryCore

interface LedgerEntryRepository {
    fun save(newEntry: LedgerEntryCore): LedgerEntry
}

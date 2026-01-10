package com.tim95bell.seecents.infrastructure.persistence.jpa.repository

import com.tim95bell.seecents.domain.model.LedgerEntry
import com.tim95bell.seecents.domain.model.LedgerEntryCore
import com.tim95bell.seecents.domain.repository.LedgerEntryRepository
import org.springframework.stereotype.Component

@Component
class JpaLedgerEntryRepository : LedgerEntryRepository {
    override fun save(newEntry: LedgerEntryCore): LedgerEntry {
        TODO("Not yet implemented")
    }
}

package com.tim95bell.seecents.infrastructure.repository

import com.tim95bell.seecents.domain.model.LedgerEntryCore
import com.tim95bell.seecents.domain.repository.LedgerEntryRepository
import org.springframework.stereotype.Component

@Component
class JPALedgerEntryRepository : LedgerEntryRepository {
    override fun save(newEntry: LedgerEntryCore) {
        TODO("Not yet implemented")
    }
}

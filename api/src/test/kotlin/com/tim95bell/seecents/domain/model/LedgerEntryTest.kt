package com.tim95bell.seecents.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LedgerEntryTest {
    @Test
    fun `can create valid entry`() {
        entry()
    }

    @Test
    fun `can NOT have 0 lines in an entry`() {
        assertThrows(IllegalArgumentException::class.java) {
            entry(lines = emptyList())
        }
    }

    @Test
    fun `can have 1 line in an entry`() {
        entry(lines = listOf(line()))
    }

    @Test
    fun `can have equal createdAt and effectiveAt in an entry`() {
        entry(createdAt = T0, effectiveAt = T0)
    }

    @Test
    fun `can have createdAt greater than effectiveAt in an entry`() {
        entry(createdAt = T1, effectiveAt = T0)
    }

    @Test
    fun `can NOT have createdAt smaller than effectiveAt in an entry`() {
        assertThrows(IllegalArgumentException::class.java) {
            entry(createdAt = T0, effectiveAt = T1)
        }
    }

    @Test
    fun `can have Expense type in an entry`() {
        entry(type = LedgerEntryType.Expense)
    }

    @Test
    fun `can have Payment type in an entry`() {
        entry(type = LedgerEntryType.Payment)
    }

    @Test
    fun `expense entries can contain lines where fromId equals toId`() {
        val lines = listOf(
            line(from = 1, to = 1)
        )
        entry(type = LedgerEntryType.Expense, lines = lines)
    }

    @Test
    fun `payment entries can NOT contain lines where fromId equals toId`() {
        val lines = listOf(
            line(from = 1, to = 1)
        )
        assertThrows(IllegalArgumentException::class.java) {
            entry(type = LedgerEntryType.Payment, lines = lines)
        }
    }

    @Test
    fun `expense entries can contain lines where fromId NOT equals toId`() {
        val lines = listOf(
            line(from = 1, to = 2)
        )
        entry(type = LedgerEntryType.Expense, lines = lines)
    }

    @Test
    fun `payment entries can contain lines where fromId NOT equals toId`() {
        val lines = listOf(
            line(from = 1, to = 2)
        )
        entry(type = LedgerEntryType.Payment, lines = lines)
    }
}

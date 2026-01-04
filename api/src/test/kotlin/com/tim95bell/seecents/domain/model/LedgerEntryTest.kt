package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*
import org.junit.jupiter.api.Test

class LedgerEntryTest {
    @Test
    fun `can create valid entry`() {
        testEntry().assertOk()
    }

    @Test
    fun `can NOT have 0 lines in an entry`() {
        testEntry(lines = emptyList()).assertErrorEq(LedgerEntryCore.CreateError.EmptyLinesError)
    }

    @Test
    fun `can have 1 line in an entry`() {
        testLine().flatMap { line ->
            testEntry(lines = listOf(line))
        }.assertOk()
    }

    @Test
    fun `can have equal createdAt and effectiveAt in an entry`() {
        testEntry(createdAt = T0, effectiveAt = T0).assertOk()
    }

    @Test
    fun `can have createdAt greater than effectiveAt in an entry`() {
        testEntry(createdAt = T1, effectiveAt = T0).assertOk()
    }

    @Test
    fun `can NOT have createdAt smaller than effectiveAt in an entry`() {
        testEntry(createdAt = T0, effectiveAt = T1).assertErrorEq(LedgerEntryCore.CreateError.EffectiveDateAfterCreationError)
    }

    @Test
    fun `can have Expense type in an entry`() {
        testEntry(type = LedgerEntryType.Expense).assertOk()
    }

    @Test
    fun `can have Payment type in an entry`() {
        testEntry(type = LedgerEntryType.Payment).assertOk()
    }

    @Test
    fun `expense entries can contain lines where fromId equals toId`() {
        testLine(from = 1, to = 1).flatMap { line ->
            testEntry(type = LedgerEntryType.Expense, lines = listOf(line))
        }.assertOk()
    }

    @Test
    fun `payment entries can NOT contain lines where fromId equals toId`() {
        testLine(from = 1, to = 1).flatMap { line ->
            testEntry(type = LedgerEntryType.Payment, lines = listOf(line))
        }.assertErrorEq(LedgerEntryCore.CreateError.PaymentFromIdEqualsToIdError)
    }

    @Test
    fun `expense entries can contain lines where fromId NOT equals toId`() {
        testLine(from = 1, to = 2).flatMap { line ->
            testEntry(type = LedgerEntryType.Expense, lines = listOf(line))
        }.assertOk()
    }

    @Test
    fun `payment entries can contain lines where fromId NOT equals toId`() {
        testLine(from = 1, to = 2).flatMap { line ->
            testEntry(type = LedgerEntryType.Payment, lines = listOf(line))
        }.assertOk()
    }
}

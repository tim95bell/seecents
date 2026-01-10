package com.tim95bell.seecents.domain.model

import arrow.core.NonEmptyList
import arrow.core.flatMap
import com.tim95bell.seecents.testutil.T0
import com.tim95bell.seecents.testutil.T1
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRight
import com.tim95bell.seecents.testutil.testEntry
import com.tim95bell.seecents.testutil.testLine
import com.tim95bell.seecents.testutil.testUserId
import org.junit.jupiter.api.Test

class LedgerEntryCoreTest {
    @Test
    fun `can create valid entry`() {
        testEntry().assertRight()
    }

    @Test
    fun `can have 1 line in an entry`() {
        testLine().flatMap { line ->
            testEntry(lines = NonEmptyList.of(line))
        }.assertRight()
    }

    @Test
    fun `can have equal createdAt and effectiveAt in an entry`() {
        testEntry(createdAt = T0, effectiveAt = T0).assertRight()
    }

    @Test
    fun `can have createdAt greater than effectiveAt in an entry`() {
        testEntry(createdAt = T1, effectiveAt = T0).assertRight()
    }

    @Test
    fun `can NOT have createdAt smaller than effectiveAt in an entry`() {
        testEntry(createdAt = T0, effectiveAt = T1)
            .assertLeftEq(LedgerEntryCore.CreateError.EffectiveDateAfterCreationError)
    }

    @Test
    fun `can have Expense type in an entry`() {
        testEntry(type = LedgerEntryType.Expense).assertRight()
    }

    @Test
    fun `can have Payment type in an entry`() {
        testEntry(type = LedgerEntryType.Payment).assertRight()
    }

    @Test
    fun `expense entries can contain lines where fromId equals toId`() {
        testLine(from = 1, to = 1).flatMap { line ->
            testEntry(type = LedgerEntryType.Expense, lines = NonEmptyList.of(line))
        }.assertRight()
    }

    @Test
    fun `payment entries can NOT contain lines where fromId equals toId`() {
        testLine(from = 1, to = 1).flatMap { line ->
            testEntry(type = LedgerEntryType.Payment, lines = NonEmptyList.of(line))
        }.assertLeftEq(LedgerEntryCore.CreateError.PaymentFromIdEqualsToIdError)
    }

    @Test
    fun `expense entries can contain lines where fromId NOT equals toId`() {
        testLine(from = 1, to = 2).flatMap { line ->
            testEntry(type = LedgerEntryType.Expense, lines = NonEmptyList.of(line))
        }.assertRight()
    }

    @Test
    fun `payment entries can contain lines where fromId NOT equals toId`() {
        testLine(from = 1, to = 2).flatMap { line ->
            testEntry(type = LedgerEntryType.Payment, lines = NonEmptyList.of(line))
        }.assertRight()
    }

    @Test
    fun `can NOT have creatorId that is not in group`() {
        testEntry(creatorId = testUserId(3)).assertLeftEq(LedgerEntryCore.CreateError.CreatorNotInGroupError)
    }

    @Test
    fun `can NOT have line fromId that is not in group`() {
        testEntry(lines = NonEmptyList.of(testLine(from = 3).assertRight().value))
            .assertLeftEq(LedgerEntryCore.CreateError.LineUserNotInGroupError)
    }

    @Test
    fun `can NOT have line toId that is not in group`() {
        testEntry(lines = NonEmptyList.of(testLine(to = 3).assertRight().value))
            .assertLeftEq(LedgerEntryCore.CreateError.LineUserNotInGroupError)
    }
}

package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.common.fp.assertError
import com.tim95bell.seecents.common.fp.assertErrorEq
import com.tim95bell.seecents.common.fp.assertOk
import com.tim95bell.seecents.domain.model.AUD
import com.tim95bell.seecents.domain.model.EUR
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.LedgerEntry
import com.tim95bell.seecents.domain.model.LedgerEntryCore
import com.tim95bell.seecents.domain.model.LedgerEntryLineCore
import com.tim95bell.seecents.domain.model.LedgerEntryType
import com.tim95bell.seecents.domain.model.MoneyAmount
import com.tim95bell.seecents.domain.model.T0
import com.tim95bell.seecents.domain.model.testGroup
import com.tim95bell.seecents.domain.model.testMoney
import com.tim95bell.seecents.domain.model.testUserId
import com.tim95bell.seecents.domain.repository.GroupRepository
import com.tim95bell.seecents.domain.repository.LedgerEntryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals

class LedgerServiceTest {
    private lateinit var ledgerRepo: LedgerEntryRepository
    private lateinit var groupRepo: GroupRepository
    private lateinit var service: LedgerService

    private fun singleLineFor(group: Group, amount: MoneyAmount = testMoney()) =
        group.core.users.toList().let { users ->
            listOf(
                LedgerService.CreateEntryLine(
                    fromId = users[0],
                    toId = users[1],
                    amount = amount
                )
            )
        }

    private fun stubGroupFound(group: Group?) {
        every { groupRepo.getById(any()) } returns group
    }

    private fun stubSaveSucceeds() {
        every { ledgerRepo.save(any()) } returns mockk<LedgerEntry>()
    }

    @BeforeEach
    fun setup() {
        ledgerRepo = mockk()
        groupRepo = mockk()
        service = LedgerService(ledgerRepo, groupRepo)
    }

    @Nested
    inner class CreateEntry {
        @Test
        fun `succeeds for valid expense entry`() {
            // GIVEN
            val group = testGroup()
            val lines = singleLineFor(group)
            stubGroupFound(group)
            stubSaveSucceeds()
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertOk()
            verify(exactly = 1) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `succeeds for valid payment entry`() {
            // GIVEN
            val group = testGroup()
            val lines = singleLineFor(group)
            stubGroupFound(group)
            stubSaveSucceeds()
            val type = LedgerEntryType.Payment

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertOk()
            verify(exactly = 1) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `fails when group does NOT exist`() {
            // GIVEN
            val group = testGroup()
            val lines = singleLineFor(group)
            stubGroupFound(null)
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertErrorEq(LedgerService.EntryCreateError.GroupNotFound(group.id))
            verify(exactly = 0) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `fails when line currency differs from group currency`() {
            // GIVEN
            val group = testGroup(currency = AUD)
            val lines = singleLineFor(group, amount = testMoney(currency = EUR))
            stubGroupFound(group)
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertErrorEq(LedgerService.EntryCreateError.CurrencyMismatch)
            verify(exactly = 0) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `fails when a line has a non positive amount`() {
            // GIVEN
            val group = testGroup()
            val lines = singleLineFor(group, amount = testMoney(amount = -1L))
            stubGroupFound(group)
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertErrorEq(LedgerService.EntryCreateError.LineError(LedgerEntryLineCore.CreateError.NonPositiveAmount))
            verify(exactly = 0) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `fails when no lines are provided`() {
            // GIVEN
            val group = testGroup()
            val lines = emptyList<LedgerService.CreateEntryLine>()
            stubGroupFound(group)
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertErrorEq(LedgerService.EntryCreateError.CoreError(LedgerEntryCore.CreateError.EmptyLinesError))
            verify(exactly = 0) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `fails when effective date is after creation date`() {
            // GIVEN
            val group = testGroup()
            val lines = singleLineFor(group)
            stubGroupFound(group)
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = Instant.now().plus(Duration.ofDays(1)),
                lines = lines,
            )

            // THEN
            result.assertErrorEq(LedgerService.EntryCreateError.CoreError(LedgerEntryCore.CreateError.EffectiveDateAfterCreationError))
            verify(exactly = 0) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `succeeds for expense entry with line where fromId equals toId`() {
            // GIVEN
            val group = testGroup()
            val user = group.core.users.first()
            val lines = listOf(
                LedgerService.CreateEntryLine(
                    fromId = user,
                    toId = user,
                    amount = testMoney(),
                )
            )
            stubGroupFound(group)
            stubSaveSucceeds()
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertOk()
            verify(exactly = 1) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `fails for payment entry with line where fromId equals toId`() {
            // GIVEN
            val group = testGroup()
            val user = group.core.users.first()
            val lines = listOf(
                LedgerService.CreateEntryLine(
                    fromId = user,
                    toId = user,
                    amount = testMoney(),
                )
            )
            stubGroupFound(group)
            val type = LedgerEntryType.Payment

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertErrorEq(
                LedgerService.EntryCreateError.CoreError(
                    LedgerEntryCore.CreateError.PaymentFromIdEqualsToIdError
                )
            )
            verify(exactly = 0) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `fails and does not save when any line is invalid`() {
            val group = testGroup()
            val users = group.core.users.toList()

            val lines = listOf(
                LedgerService.CreateEntryLine(users[0], users[1], testMoney()),
                LedgerService.CreateEntryLine(users[1], users[0], testMoney(amount = -1))
            )

            stubGroupFound(group)

            val result = service.createEntry(
                type = LedgerEntryType.Expense,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            result.assertError()
            verify(exactly = 0) { ledgerRepo.save(any()) }
        }

        @Test
        fun `does not validate lines when group does not exist`() {
            stubGroupFound(null)

            val result = service.createEntry(
                type = LedgerEntryType.Expense,
                groupId = testGroup().id,
                creatorId = testUserId(),
                effectiveAt = T0,
                lines = emptyList()
            )

            result.assertErrorEq(
                LedgerService.EntryCreateError.GroupNotFound(testGroup().id)
            )
        }

        @Test
        fun `fails when creator is not in group`() {
            // GIVEN
            val group = testGroup()
            val lines = singleLineFor(group)
            stubGroupFound(group)
            stubSaveSucceeds()
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(3),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            assertEquals(
                LedgerService.EntryCreateError.CoreError(LedgerEntryCore.CreateError.CreatorNotInGroupError),
                result.assertError().error
            )
            verify(exactly = 0) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `fails when line fromId is not in group`() {
            // GIVEN
            val group = testGroup()
            val lines = listOf(
                LedgerService.CreateEntryLine(
                    fromId = testUserId(3),
                    toId = testUserId(2),
                    amount = testMoney(),
                )
            )
            stubGroupFound(group)
            stubSaveSucceeds()
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertErrorEq(LedgerService.EntryCreateError.CoreError(LedgerEntryCore.CreateError.LineUserNotInGroupError))
            verify(exactly = 0) {
                ledgerRepo.save(any())
            }
        }

        @Test
        fun `fails when line toId is not in group`() {
            // GIVEN
            val group = testGroup()
            val lines = listOf(
                LedgerService.CreateEntryLine(
                    fromId = testUserId(1),
                    toId = testUserId(3),
                    amount = testMoney(),
                )
            )
            stubGroupFound(group)
            stubSaveSucceeds()
            val type = LedgerEntryType.Expense

            // WHEN
            val result = service.createEntry(
                type = type,
                creatorId = testUserId(1),
                groupId = group.id,
                effectiveAt = T0,
                lines = lines,
            )

            // THEN
            result.assertErrorEq(LedgerService.EntryCreateError.CoreError(LedgerEntryCore.CreateError.LineUserNotInGroupError))
            verify(exactly = 0) {
                ledgerRepo.save(any())
            }
        }
    }
}
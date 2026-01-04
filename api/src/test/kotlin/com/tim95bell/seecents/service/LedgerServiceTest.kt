package com.tim95bell.seecents.service

import com.tim95bell.seecents.application.service.LedgerService
import com.tim95bell.seecents.common.fp.*
import com.tim95bell.seecents.domain.model.AUD
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
import com.tim95bell.seecents.domain.model.LedgerEntryCore
import com.tim95bell.seecents.domain.model.LedgerEntryType
import com.tim95bell.seecents.domain.model.T0
import com.tim95bell.seecents.domain.model.testGroup
import com.tim95bell.seecents.domain.model.testMoney
import com.tim95bell.seecents.domain.model.testUser
import com.tim95bell.seecents.domain.repository.GroupRepository
import com.tim95bell.seecents.domain.repository.LedgerEntryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LedgerServiceTest {
    private lateinit var ledgerRepo: LedgerEntryRepository
    private lateinit var groupRepo: GroupRepository
    private lateinit var service: LedgerService

    @BeforeEach
    fun setup() {
        ledgerRepo = mockk()
        groupRepo = mockk()
        service = LedgerService(ledgerRepo, groupRepo)
    }

    @Test
    fun `record expense for a group`() {
        // GIVEN
        val groupId = testGroup()
        val u1 = testUser(1)
        val u2 = testUser(2)
        val group = Group(
            id = groupId,
            core = GroupCore(
                currency = AUD,
                users = setOf(u1, u2)
            )
        )
        val amount = testMoney()

        every { groupRepo.getGroupById(groupId) } returns group

        val savedEntrySlot = slot<LedgerEntryCore>()

        val lines = listOf(
            LedgerService.CreateEntryLine(
                fromId = u1,
                toId = u2,
                amount = amount,
            )
        )

        val now = T0

        every { ledgerRepo.save(capture(savedEntrySlot)) } returns Unit

        // WHEN
        val result = service.createEntry(
            type = LedgerEntryType.Expense,
            groupId = groupId,
            effectiveAt = now,
            lines = lines,
        )

        // THEN
        result.assertOk().tap {
            assertEquals(LedgerEntryType.Expense, it.type)
            assertEquals(groupId, it.groupId)
            assertEquals(now, it.effectiveAt)
            assertEquals(lines.size, it.lines.size)
            for ((createLine, resultLine) in lines.zip(it.lines)) {
                assertEquals(createLine.fromId, resultLine.fromId)
                assertEquals(createLine.toId, resultLine.toId)
                assertEquals(createLine.amount, resultLine.amount)
            }
        }
        verify(exactly = 1) {
            ledgerRepo.save(savedEntrySlot.captured)
        }
        verify(exactly = 1) {
            groupRepo.getGroupById(groupId)
        }

        val saved = savedEntrySlot.captured

        assertEquals(groupId, saved.groupId)
        assertEquals(LedgerEntryType.Expense, saved.type)
        assertEquals(1, saved.lines.size)
        val line = saved.lines.first()
        assertEquals(u1, line.fromId)
        assertEquals(u2, line.toId)
        assertEquals(amount, line.amount)
    }
}

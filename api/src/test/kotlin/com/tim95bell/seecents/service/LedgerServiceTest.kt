package com.tim95bell.seecents.service

import com.tim95bell.seecents.application.service.LedgerService
import com.tim95bell.seecents.domain.model.AUD
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
import com.tim95bell.seecents.domain.model.LedgerEntryCore
import com.tim95bell.seecents.domain.model.LedgerEntryType
import com.tim95bell.seecents.domain.model.group
import com.tim95bell.seecents.domain.model.money
import com.tim95bell.seecents.domain.model.user
import com.tim95bell.seecents.domain.repository.GroupRepository
import com.tim95bell.seecents.domain.repository.LedgerEntryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
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
        val groupId = group()
        val u1 = user(1)
        val u2 = user(2)
        val group = Group(
            id = groupId,
            core = GroupCore(
                currency = AUD,
                users = setOf(u1, u2)
            )
        )
        val amount = money()

        every { groupRepo.getGroupById(groupId) } returns group

        val savedEntrySlot = slot<LedgerEntryCore>()

        val lines = listOf(
            LedgerService.CreateEntryLine(
                fromId = u1,
                toId = u2,
                amount = amount,
            )
        )

        val now = Instant.now()

        every { ledgerRepo.save(capture(savedEntrySlot)) } returns Unit

        // WHEN
        service.createEntry(
            type = LedgerEntryType.Expense,
            groupId = groupId,
            effectiveAt = now,
            lines = lines,
        )

        // THEN
        verify(exactly = 1) {
            ledgerRepo.save(any())
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

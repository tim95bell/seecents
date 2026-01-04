package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.domain.model.GroupId
import com.tim95bell.seecents.domain.model.LedgerEntryCore
import com.tim95bell.seecents.domain.model.LedgerEntryLineCore
import com.tim95bell.seecents.domain.model.LedgerEntryType
import com.tim95bell.seecents.domain.model.MoneyAmount
import com.tim95bell.seecents.domain.model.UserId
import com.tim95bell.seecents.domain.repository.GroupRepository
import com.tim95bell.seecents.domain.repository.LedgerEntryRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class LedgerService(
    private val ledgerEntryRepository: LedgerEntryRepository,
    private val groupRepository: GroupRepository,
) {
    data class CreateEntryLine(
        val fromId: UserId,
        val toId: UserId,
        val amount: MoneyAmount,
    )

    fun createEntry(
        type: LedgerEntryType,
        groupId: GroupId,
        effectiveAt: Instant,
        lines: List<CreateEntryLine>,
    ) {
        val now = Instant.now()
        if (effectiveAt > now) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "effectiveAt must be <= now")
        }

        val group = groupRepository.getGroupById(groupId)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Group with id $groupId not found")

        if (lines.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Lines cannot be empty")
        }

        if (lines.any {
            it.amount.currency != group.core.currency
        }) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "All lines must have the same currency")
        }

        when (type) {
            LedgerEntryType.Payment -> {
                if (lines.any {
                    it.toId == it.fromId
                }) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A payment cannot be made to the same user as it is from")
                }
            }
            LedgerEntryType.Expense -> {}
        }

        if (lines.any {
            !group.core.users.contains(it.fromId) || !group.core.users.contains(it.toId)
        }) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "All users must belong to the group")
        }

        val newEntry = LedgerEntryCore(
            groupId = groupId,
            type = type,
            createdAt = now,
            effectiveAt = effectiveAt,
            lines = lines.map {
                LedgerEntryLineCore(
                    fromId = it.fromId,
                    toId = it.toId,
                    amount = it.amount,
                )
            }
        )

        ledgerEntryRepository.save(newEntry)
    }
}

package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.common.fp.*
import com.tim95bell.seecents.domain.model.GroupId
import com.tim95bell.seecents.domain.model.LedgerEntryCore
import com.tim95bell.seecents.domain.model.LedgerEntryLineCore
import com.tim95bell.seecents.domain.model.LedgerEntryType
import com.tim95bell.seecents.domain.model.MoneyAmount
import com.tim95bell.seecents.domain.model.UserId
import com.tim95bell.seecents.domain.repository.GroupRepository
import com.tim95bell.seecents.domain.repository.LedgerEntryRepository
import org.springframework.stereotype.Service
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

    sealed interface EntryCreateError {
        data class LineError(val lineError: LedgerEntryLineCore.CreateError): EntryCreateError
        data class CoreError(val coreError: LedgerEntryCore.CreateError): EntryCreateError
        data class GroupNotFound(val groupId: GroupId): EntryCreateError
        data object CurrencyMismatch: EntryCreateError
    }

    fun createEntry(
        type: LedgerEntryType,
        groupId: GroupId,
        effectiveAt: Instant,
        lines: List<CreateEntryLine>,
    ): Result<EntryCreateError, LedgerEntryCore> {
        val now = Instant.now()

        val group = groupRepository.getGroupById(groupId)
            ?: return Result.Error(EntryCreateError.GroupNotFound(groupId))

        if (lines.any {
            it.amount.currency != group.core.currency
        }) {
            return Result.Error(EntryCreateError.CurrencyMismatch)
        }

        return lines.map {
            LedgerEntryLineCore.create(
                fromId = it.fromId,
                toId = it.toId,
                amount = it.amount,
            ).mapError {
                EntryCreateError.LineError(it)
            }
        }.sequence().flatMap { lines ->
            LedgerEntryCore.create(
                groupId = groupId,
                type = type,
                createdAt = now,
                effectiveAt = effectiveAt,
                lines = lines,
            ).mapError {
                EntryCreateError.CoreError(it)
            }
        }.tap {
            ledgerEntryRepository.save(it)
        }
    }
}

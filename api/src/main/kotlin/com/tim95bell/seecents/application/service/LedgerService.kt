package com.tim95bell.seecents.application.service

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.either
import arrow.core.toNonEmptySetOrNone
import com.tim95bell.seecents.domain.model.GroupId
import com.tim95bell.seecents.domain.model.LedgerEntry
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
    private val ledgerEntryRepo: LedgerEntryRepository,
    private val groupRepo: GroupRepository,
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
        data object EmptyLines : EntryCreateError
    }

    fun createEntry(
        type: LedgerEntryType,
        groupId: GroupId,
        creatorId: UserId,
        effectiveAt: Instant,
        lines: List<CreateEntryLine>,
    ): Either<EntryCreateError, LedgerEntry> {
        val now = Instant.now()

        val group = groupRepo.getById(groupId)
            ?: return EntryCreateError.GroupNotFound(groupId).left()

        if (lines.any {
            it.amount.currency != group.core.currency
        }) {
            return EntryCreateError.CurrencyMismatch.left()
        }

        return either {
            val createLines = lines.toNonEmptySetOrNone().toEither {
                EntryCreateError.EmptyLines
            }.bind()

            val lines = createLines.map {
                LedgerEntryLineCore.create(
                    fromId = it.fromId,
                    toId = it.toId,
                    amount = it.amount,
                ).mapLeft(EntryCreateError::LineError)
            }.let { either { it.bindAll() } }.bind()

            val core = LedgerEntryCore.create(
                group = group,
                creatorId = creatorId,
                type = type,
                createdAt = now,
                effectiveAt = effectiveAt,
                lines = lines,
            ).mapLeft {
                EntryCreateError.CoreError(it)
            }.bind()

            ledgerEntryRepo.save(core)
        }
    }
}

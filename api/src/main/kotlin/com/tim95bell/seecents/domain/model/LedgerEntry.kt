package com.tim95bell.seecents.domain.model

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.right
import java.time.Instant

@ConsistentCopyVisibility
data class LedgerEntry private constructor(
    val id: LedgerEntryId,
    val type: LedgerEntryType,
    val groupId: GroupId,
    val creatorId: UserId,
    val createdAt: Instant,
    val effectiveAt: Instant,
    val lines: NonEmptyList<LedgerEntryLine>,
) {
    sealed interface CreateError {
        data object EffectiveDateAfterCreationError : CreateError
        data object PaymentFromIdEqualsToIdError : CreateError
        data object CreatorNotInGroupError : CreateError
        data object LineUserNotInGroupError : CreateError
    }

    companion object {
        fun create(
            id: LedgerEntryId,
            type: LedgerEntryType,
            group: Group,
            creatorId: UserId,
            createdAt: Instant,
            effectiveAt: Instant,
            lines: NonEmptyList<LedgerEntryLine>,
        ): Either<CreateError, LedgerEntry> {
            if (!group.users.contains(creatorId)) {
                return CreateError.CreatorNotInGroupError.left()
            }

            if (lines.any {
                !group.users.contains(it.fromId) ||
                        !group.users.contains(it.toId)
            }) {
                return CreateError.LineUserNotInGroupError.left()
            }

            if (effectiveAt > createdAt) {
                return CreateError.EffectiveDateAfterCreationError.left()
            }

            when (type) {
                LedgerEntryType.Payment -> {
                    if (lines.any { it.fromId == it.toId }) {
                        return CreateError.PaymentFromIdEqualsToIdError.left()
                    }
                }
                LedgerEntryType.Expense -> {
                }
            }

            return LedgerEntry(
                id = id,
                type = type,
                groupId = group.id,
                creatorId = creatorId,
                createdAt = createdAt,
                effectiveAt = effectiveAt,
                lines = lines
            ).right()
        }
    }
}

package com.tim95bell.seecents.domain.model

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.right
import java.time.Instant

@ConsistentCopyVisibility
data class LedgerEntryCore private constructor(
    val type: LedgerEntryType,
    val groupId: GroupId,
    val creatorId: UserId,
    val createdAt: Instant,
    val effectiveAt: Instant,
    val lines: NonEmptyList<LedgerEntryLineCore>,
) {
    sealed interface CreateError {
        data object EffectiveDateAfterCreationError : CreateError
        data object PaymentFromIdEqualsToIdError : CreateError
        data object CreatorNotInGroupError : CreateError
        data object LineUserNotInGroupError : CreateError
    }

    companion object {
        fun create(
            type: LedgerEntryType,
            group: Group,
            creatorId: UserId,
            createdAt: Instant,
            effectiveAt: Instant,
            lines: NonEmptyList<LedgerEntryLineCore>,
        ): Either<CreateError, LedgerEntryCore> {
            if (!group.core.users.contains(creatorId)) {
                return CreateError.CreatorNotInGroupError.left()
            }

            if (lines.any {
                !group.core.users.contains(it.fromId) ||
                        !group.core.users.contains(it.toId)
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

            return LedgerEntryCore(
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

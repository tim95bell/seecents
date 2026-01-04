package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.Result
import java.time.Instant

data class LedgerEntryCore private constructor(
    val type: LedgerEntryType,
    val groupId: GroupId,
    val createdAt: Instant,
    val effectiveAt: Instant,
    val lines: List<LedgerEntryLineCore>,
) {
    sealed interface CreateError {
        data object EmptyLinesError : CreateError
        data object EffectiveDateAfterCreationError : CreateError
        data object PaymentFromIdEqualsToIdError : CreateError
    }

    companion object {
        fun create(
            type: LedgerEntryType,
            groupId: GroupId,
            createdAt: Instant,
            effectiveAt: Instant,
            lines: List<LedgerEntryLineCore>,
        ): Result<CreateError, LedgerEntryCore> {
            if (lines.isEmpty()) {
                return Result.Error(CreateError.EmptyLinesError)
            }

            if (effectiveAt > createdAt) {
                return Result.Error(CreateError.EffectiveDateAfterCreationError)
            }

            when (type) {
                LedgerEntryType.Payment -> {
                    if (lines.any { it.fromId == it.toId }) {
                        return Result.Error(CreateError.PaymentFromIdEqualsToIdError)
                    }
                }
                LedgerEntryType.Expense -> {
                }
            }

            return Result.Ok(LedgerEntryCore(
                type = type,
                groupId = groupId,
                createdAt = createdAt,
                effectiveAt = effectiveAt,
                lines = lines
            ))
        }
    }
}

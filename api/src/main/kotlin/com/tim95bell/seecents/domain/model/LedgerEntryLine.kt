package com.tim95bell.seecents.domain.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right

@ConsistentCopyVisibility
data class LedgerEntryLine private constructor(
    val id: LedgerEntryLineId,
    val fromId: UserId,
    val toId: UserId,
    val amount: MoneyAmount,
) {
    sealed interface CreateError {
        data object NonPositiveAmount : CreateError
    }

    companion object {
        fun create(
            id: LedgerEntryLineId,
            fromId: UserId,
            toId: UserId,
            amount: MoneyAmount,
        ) : Either<CreateError, LedgerEntryLine> {
            if (amount.amount <= 0) {
                return CreateError.NonPositiveAmount.left()
            }

            return LedgerEntryLine(id, fromId, toId, amount).right()
        }
    }
}

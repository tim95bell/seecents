package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*

data class LedgerEntryLineCore private constructor(
    val fromId: UserId,
    val toId: UserId,
    val amount: MoneyAmount,
) {
    sealed interface CreateError {
        data object NonPositiveAmount : CreateError
    }

    companion object {
        fun create(
            fromId: UserId,
            toId: UserId,
            amount: MoneyAmount,
        ) : Result<CreateError, LedgerEntryLineCore> {
            if (amount.amount <= 0) {
                return error(CreateError.NonPositiveAmount)
            }

            return ok(LedgerEntryLineCore(fromId, toId, amount))
        }
    }
}

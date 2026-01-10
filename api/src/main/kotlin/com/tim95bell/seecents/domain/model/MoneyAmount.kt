package com.tim95bell.seecents.domain.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.Currency

data class MoneyAmount(
    val currency: Currency,
    val amount: Long,
) {
    sealed interface CombineError {
        data object CombineDifferingCurrenciesError : CombineError
    }

    fun combine(other: MoneyAmount, block: (Long, Long) -> Long): Either<CombineError, MoneyAmount> {
        if (this.currency != other.currency) {
            return CombineError.CombineDifferingCurrenciesError.left()
        }

        return MoneyAmount(currency, block(amount, other.amount)).right()
    }
}

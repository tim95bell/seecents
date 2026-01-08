package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*
import java.util.Currency

data class MoneyAmount(
    val currency: Currency,
    val amount: Long,
) {
    sealed interface CombineError {
        data object CombineDifferingCurrenciesError : CombineError
    }

    fun combine(other: MoneyAmount, block: (Long, Long) -> Long): Result<CombineError, MoneyAmount> {
        if (this.currency != other.currency) {
            return error(CombineError.CombineDifferingCurrenciesError)
        }

        return ok(MoneyAmount(currency, block(amount, other.amount)))
    }
}

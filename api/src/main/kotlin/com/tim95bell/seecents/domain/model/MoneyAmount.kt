package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.Result
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
            return Result.Error(CombineError.CombineDifferingCurrenciesError)
        }

        return Result.Ok(MoneyAmount(currency, block(amount, other.amount)))
    }
}

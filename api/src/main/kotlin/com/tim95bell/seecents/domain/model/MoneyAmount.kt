package com.tim95bell.seecents.domain.model

import java.util.Currency

data class MoneyAmount(
    val currency: Currency,
    val amount: Long,
) {
    fun combine(other: MoneyAmount, block: (Long, Long) -> Long): MoneyAmount {
        require(this.currency == other.currency)
        return MoneyAmount(currency, block(amount, other.amount))
    }
}

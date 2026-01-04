package com.tim95bell.seecents.domain.model

data class LedgerEntryLineCore(
    val fromId: UserId,
    val toId: UserId,
    val amount: MoneyAmount,
) {
    init {
        require(amount.amount > 0)
    }
}

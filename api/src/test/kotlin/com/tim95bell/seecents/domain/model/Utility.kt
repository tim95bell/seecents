package com.tim95bell.seecents.domain.model

import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.Currency

const val DEFAULT_MONEY_AMOUNT = 10L
val AUD: Currency = Currency.getInstance("AUD")
val EUR: Currency = Currency.getInstance("EUR")
val T0: Instant = Instant.parse("2025-01-01T00:00:00Z")
val T1: Instant = T0.plus(Duration.ofDays(1))

fun money(currency: Currency = AUD, amount: Long = DEFAULT_MONEY_AMOUNT) = MoneyAmount(currency, amount)

fun user(id: Int = 1) = UserId("u$id")

fun group(id: Int = 1) = GroupId("g$id")

fun line(
    from: Int = 1,
    to: Int = 2,
    amount: Long = DEFAULT_MONEY_AMOUNT
) = LedgerEntryLineCore(
    user(from),
    user(to),
    money(amount = amount)
)

fun entry(
    type: LedgerEntryType = LedgerEntryType.Expense,
    createdAt: Instant = T0,
    effectiveAt: Instant = T0,
    lines: List<LedgerEntryLineCore>? = null
) = LedgerEntryCore(
    type,
    group(),
    createdAt,
    effectiveAt,
    lines ?: listOf(line()),
)

class UtilityTest {
    @Test
    fun `T0 is smaller than T1`() {
        assert(T0 < T1)
    }
}

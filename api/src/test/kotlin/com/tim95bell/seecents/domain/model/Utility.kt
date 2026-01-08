package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.Currency

const val DEFAULT_MONEY_AMOUNT = 10L
val AUD: Currency = Currency.getInstance("AUD")
val EUR: Currency = Currency.getInstance("EUR")
val T0: Instant = Instant.parse("2025-01-01T00:00:00Z")
val T1: Instant = T0.plus(Duration.ofDays(1))

fun testMoney(currency: Currency = AUD, amount: Long = DEFAULT_MONEY_AMOUNT) = MoneyAmount(currency, amount)

fun testUserId(id: Int = 1) = UserId("u$id")

fun testGroupId(id: Int = 1) = GroupId("g$id")

fun testGroup(
    id: Int = 1,
    currency: Currency = AUD,
    users: Set<UserId> = setOf(testUserId(1), testUserId(2))
) = Group(testGroupId(id), GroupCore.create(
    currency = currency,
    name = "test",
    users = users,
).assertOk().value)

fun testLine(
    from: Int = 1,
    to: Int = 2,
    amount: Long = DEFAULT_MONEY_AMOUNT
) = LedgerEntryLineCore.create(
    testUserId(from),
    testUserId(to),
    testMoney(amount = amount)
)

fun testEntry(
    type: LedgerEntryType = LedgerEntryType.Expense,
    createdAt: Instant = T0,
    effectiveAt: Instant = T0,
    lines: List<LedgerEntryLineCore>? = null
) = testLine().flatMap {
    LedgerEntryCore.create(
        type,
        testGroupId(),
        createdAt,
        effectiveAt,
        lines ?: listOf(testLine().assertOk().value)
    )
}

class UtilityTest {
    @Test
    fun `T0 is smaller than T1`() {
        assert(T0 < T1)
    }
}

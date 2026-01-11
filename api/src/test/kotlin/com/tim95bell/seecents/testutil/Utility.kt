package com.tim95bell.seecents.testutil

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import com.tim95bell.seecents.domain.model.Email
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupId
import com.tim95bell.seecents.domain.model.GroupName
import com.tim95bell.seecents.domain.model.LedgerEntry
import com.tim95bell.seecents.domain.model.LedgerEntryId
import com.tim95bell.seecents.domain.model.LedgerEntryLine
import com.tim95bell.seecents.domain.model.LedgerEntryLineId
import com.tim95bell.seecents.domain.model.LedgerEntryType
import com.tim95bell.seecents.domain.model.MoneyAmount
import com.tim95bell.seecents.domain.model.PasswordHash
import com.tim95bell.seecents.domain.model.User
import com.tim95bell.seecents.domain.model.UserId
import com.tim95bell.seecents.domain.model.UserName
import com.tim95bell.seecents.util.unwrap
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.Currency
import kotlin.test.assertEquals

fun <L, R> Either<L, R>.assertLeft(): Either.Left<L> {
    require(this is Either.Left<L>)
    return this
}

fun <L, R> Either<L, R>.assertRight(): Either.Right<R> {
    require(this is Either.Right<R>)
    return this
}

fun <L, R> Either<L, R>.assertLeftEq(expected: L): Either.Left<L> {
    require(this is Either.Left<L>)
    assertEquals(expected, this.value)
    return this
}

fun <L, R> Either<L, R>.assertRightEq(expected: R): Either.Right<R> {
    require(this is Either.Right<R>)
    assertEquals(expected, this.value)
    return this
}

const val DEFAULT_MONEY_AMOUNT = 10L
val AUD: Currency = Currency.getInstance("AUD")
val EUR: Currency = Currency.getInstance("EUR")
val T0: Instant = Instant.parse("2025-01-01T00:00:00Z")
val T1: Instant = T0.plus(Duration.ofDays(1))
val U1 = UserId.new()
val U2 = UserId.new()
val U3 = UserId.new()

fun testMoney(currency: Currency = AUD, amount: Long = DEFAULT_MONEY_AMOUNT) = MoneyAmount(currency, amount)

fun testUser(
    id: UserId = U1,
    name: String = "test",
    email: String = "test@gmail.com",
    passwordHash: String = "password"
): User {
    return User(
        id,
        UserName.fromCanonical(name).unwrap(),
        Email.fromCanonical(email).unwrap(),
        PasswordHash(passwordHash),
    )
}

fun testGroup(
    id: GroupId = GroupId.new(),
    currency: Currency = AUD,
    users: NonEmptySet<UserId> = NonEmptySet.of(U1, U2),
) = Group(
    id,
    currency = currency,
    name = GroupName.fromCanonical("test")
        .assertRight().value,
    users = users,
)

fun testLine(
    id: LedgerEntryLineId = LedgerEntryLineId.new(),
    from: UserId = U1,
    to: UserId = U2,
    amount: Long = DEFAULT_MONEY_AMOUNT
) = LedgerEntryLine.create(
    id,
    from,
    to,
    testMoney(amount = amount)
)

fun testEntry(
    id: LedgerEntryId = LedgerEntryId.new(),
    type: LedgerEntryType = LedgerEntryType.Expense,
    createdAt: Instant = T0,
    effectiveAt: Instant = T0,
    group: Group = Group(
        GroupId.new(),
        GroupName.fromCanonical("test")
            .assertRight().value,
        AUD,
        NonEmptySet.of(U1, U2),
    ),
    lines: NonEmptyList<LedgerEntryLine> = NonEmptyList.of(
        LedgerEntryLine.create(
            LedgerEntryLineId.new(),
            U1,
            U2,
            testMoney()
        ).assertRight().value
    ),
    creatorId: UserId = U1,
): Either<LedgerEntry.CreateError, LedgerEntry> {
    return LedgerEntry.create(
        id,
        type,
        group,
        creatorId,
        createdAt,
        effectiveAt,
        lines,
    )
}

class UtilityTest {
    @Test
    fun `T0 is smaller than T1`() {
        assert(T0 < T1)
    }
}

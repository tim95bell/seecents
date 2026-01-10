package com.tim95bell.seecents.testutil

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
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

fun testMoney(currency: Currency = AUD, amount: Long = DEFAULT_MONEY_AMOUNT) =
    _root_ide_package_.com.tim95bell.seecents.domain.model.MoneyAmount(currency, amount)

fun testUserId(id: Int = 1) = _root_ide_package_.com.tim95bell.seecents.domain.model.UserId("u$id")

fun testGroupId(id: Int = 1) = _root_ide_package_.com.tim95bell.seecents.domain.model.GroupId("g$id")

fun testUserCore(name: String = "test", email: String = "test@gmail.com", passwordHash: String = "password"): com.tim95bell.seecents.domain.model.UserCore {
    return _root_ide_package_.com.tim95bell.seecents.domain.model.UserCore(
        _root_ide_package_.com.tim95bell.seecents.domain.model.UserName.Companion.fromCanonical(name)
            .assertRight().value,
        _root_ide_package_.com.tim95bell.seecents.domain.model.Email.Companion.fromCanonical(email).assertRight().value,
        _root_ide_package_.com.tim95bell.seecents.domain.model.PasswordHash(passwordHash),
    )
}

fun testUser(
    id: Int = 1,
    name: String = "test",
    email: String = "test@gmail.com",
    passwordHash: String = "password"
): com.tim95bell.seecents.domain.model.User {
    return _root_ide_package_.com.tim95bell.seecents.domain.model.User(
        testUserId(id),
        testUserCore(name = name, email = email, passwordHash = passwordHash)
    )
}

fun testGroup(
    id: Int = 1,
    currency: Currency = AUD,
    users: NonEmptySet<com.tim95bell.seecents.domain.model.UserId> = NonEmptySet.of(testUserId(1), testUserId(2))
) = _root_ide_package_.com.tim95bell.seecents.domain.model.Group(
    testGroupId(id), _root_ide_package_.com.tim95bell.seecents.domain.model.GroupCore(
        currency = currency,
        name = _root_ide_package_.com.tim95bell.seecents.domain.model.GroupName.Companion.fromCanonical("test")
            .assertRight().value,
        users = users,
    )
)

fun testLine(
    from: Int = 1,
    to: Int = 2,
    amount: Long = DEFAULT_MONEY_AMOUNT
) = _root_ide_package_.com.tim95bell.seecents.domain.model.LedgerEntryLineCore.Companion.create(
    testUserId(from),
    testUserId(to),
    testMoney(amount = amount)
)

fun testEntry(
    type: com.tim95bell.seecents.domain.model.LedgerEntryType = _root_ide_package_.com.tim95bell.seecents.domain.model.LedgerEntryType.Expense,
    createdAt: Instant = T0,
    effectiveAt: Instant = T0,
    group: com.tim95bell.seecents.domain.model.Group = _root_ide_package_.com.tim95bell.seecents.domain.model.Group(
        testGroupId(), _root_ide_package_.com.tim95bell.seecents.domain.model.GroupCore(
            _root_ide_package_.com.tim95bell.seecents.domain.model.GroupName.Companion.fromCanonical("test")
                .assertRight().value,
            AUD,
            NonEmptySet.of(testUserId(1), testUserId(2)),
        )
    ),
    lines: NonEmptyList<com.tim95bell.seecents.domain.model.LedgerEntryLineCore> = NonEmptyList.of(
        _root_ide_package_.com.tim95bell.seecents.domain.model.LedgerEntryLineCore.Companion.create(
            testUserId(1),
            testUserId(2),
            testMoney()
        ).assertRight().value
    ),
    creatorId: com.tim95bell.seecents.domain.model.UserId = testUserId(1),
): Either<com.tim95bell.seecents.domain.model.LedgerEntryCore.CreateError, com.tim95bell.seecents.domain.model.LedgerEntryCore> {
    return _root_ide_package_.com.tim95bell.seecents.domain.model.LedgerEntryCore.Companion.create(
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

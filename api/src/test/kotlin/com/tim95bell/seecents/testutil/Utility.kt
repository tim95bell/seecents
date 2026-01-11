package com.tim95bell.seecents.testutil

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import com.tim95bell.seecents.domain.model.Email
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
import com.tim95bell.seecents.domain.model.GroupId
import com.tim95bell.seecents.domain.model.GroupName
import com.tim95bell.seecents.domain.model.LedgerEntryCore
import com.tim95bell.seecents.domain.model.LedgerEntryLineCore
import com.tim95bell.seecents.domain.model.LedgerEntryType
import com.tim95bell.seecents.domain.model.MoneyAmount
import com.tim95bell.seecents.domain.model.PasswordHash
import com.tim95bell.seecents.domain.model.User
import com.tim95bell.seecents.domain.model.UserCore
import com.tim95bell.seecents.domain.model.UserId
import com.tim95bell.seecents.domain.model.UserName
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

fun testMoney(currency: Currency = AUD, amount: Long = DEFAULT_MONEY_AMOUNT) = MoneyAmount(currency, amount)

fun testUserId(id: Int = 1) = UserId("u$id")

fun testGroupId(id: Int = 1) = GroupId("g$id")

fun testUserCore(name: String = "test", email: String = "test@gmail.com", passwordHash: String = "password"): UserCore {
    return UserCore(
        UserName.fromCanonical(name)
            .assertRight().value,
        Email.fromCanonical(email).assertRight().value,
        PasswordHash(passwordHash),
    )
}

fun testUser(
    id: Int = 1,
    name: String = "test",
    email: String = "test@gmail.com",
    passwordHash: String = "password"
): User {
    return User(
        testUserId(id),
        testUserCore(name = name, email = email, passwordHash = passwordHash)
    )
}

fun testGroup(
    id: Int = 1,
    currency: Currency = AUD,
    users: NonEmptySet<UserId> = NonEmptySet.of(testUserId(1), testUserId(2))
) = Group(
    testGroupId(id), GroupCore(
        currency = currency,
        name = GroupName.fromCanonical("test")
            .assertRight().value,
        users = users,
    )
)

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
    group: Group = Group(
        testGroupId(), GroupCore(
            GroupName.fromCanonical("test")
                .assertRight().value,
            AUD,
            NonEmptySet.of(testUserId(1), testUserId(2)),
        )
    ),
    lines: NonEmptyList<LedgerEntryLineCore> = NonEmptyList.of(
        LedgerEntryLineCore.create(
            testUserId(1),
            testUserId(2),
            testMoney()
        ).assertRight().value
    ),
    creatorId: UserId = testUserId(1),
): Either<LedgerEntryCore.CreateError, LedgerEntryCore> {
    return LedgerEntryCore.create(
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

package com.tim95bell.seecents.testutil

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.getOrElse
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.Currency
import kotlin.test.assertEquals
import kotlin.test.fail

fun <L, R> Either<L, R>.assertLeft(): Either.Left<L> = when (this) {
    is Either.Left -> this
    is Either.Right -> fail("Expected Left, but was Right($value)")
}

fun <L, R> Either<L, R>.assertRight(): Either.Right<R> = when (this) {
    is Either.Right -> this
    is Either.Left -> fail("Expected Right, but was Left($value)")
}

fun <L, R> Either<L, R>.assertLeftEq(expected: L): Either.Left<L> = assertLeft().also {
    assertEquals(expected, it.value)
}

fun <L, R> Either<L, R>.assertRightEq(expected: R): Either.Right<R> = assertRight().also {
    assertEquals(expected, it.value)
}

val TEST_MONEY_AMOUNT: List<Long> = listOf(10L, 20L, 30L)
val TEST_CURRENCY: List<Currency> = listOf("AUD", "EUR", "USD").map { Currency.getInstance(it) }
val TEST_INSTANT: List<Instant> = Instant.parse("2025-01-01T00:00:00Z").let {
    listOf(it, it.plus(Duration.ofDays(1)), it.plus(Duration.ofDays(2)))
}
val TEST_USER_ID: List<UserId> = List(3) { UserId.new() }
val TEST_GROUP_ID: List<GroupId> = List(3) { GroupId.new() }
val TEST_ENTRY_ID: List<LedgerEntryId> = List(3) { LedgerEntryId.new() }
val TEST_LINE_ID: List<LedgerEntryLineId> = List(3) { LedgerEntryLineId.new() }
val TEST_USER_NAME: List<UserName> = listOf("test", "name2", "other name")
    .map { UserName.fromCanonical(it).unwrap() }
val TEST_GROUP_NAME: List<GroupName> = listOf("test", "name2", "other name")
    .map { GroupName.fromCanonical(it).unwrap() }
val TEST_EMAIL: List<Email> = listOf("test@gmail.com", "floating_points@test.com", "pharoah.sanders@yahoo.com")
    .map { Email.fromCanonical(it).unwrap() }
val TEST_PASSWORD_HASH: List<PasswordHash> = listOf("password", "hash", "passwordHash").map { PasswordHash(it) }

fun testMoney(currency: Currency = TEST_CURRENCY[0], amount: Long = TEST_MONEY_AMOUNT[0]) =
    MoneyAmount(currency, amount)

fun testUser(
    id: UserId = TEST_USER_ID[0],
    name: UserName = TEST_USER_NAME[0],
    email: Email = TEST_EMAIL[0],
    passwordHash: PasswordHash = TEST_PASSWORD_HASH[0],
) = User(
    id,
    name,
    email,
    passwordHash,
)

fun testGroup(
    id: GroupId = TEST_GROUP_ID[0],
    currency: Currency = TEST_CURRENCY[0],
    users: NonEmptySet<UserId> = NonEmptySet.of(TEST_USER_ID[0], TEST_USER_ID[1]),
    name: GroupName = TEST_GROUP_NAME[0],
) = Group(
    id,
    currency = currency,
    name = name,
    users = users,
)

fun testLine(
    id: LedgerEntryLineId = TEST_LINE_ID[0],
    from: UserId = TEST_USER_ID[0],
    to: UserId = TEST_USER_ID[1],
    amount: Long = TEST_MONEY_AMOUNT[0],
    currency: Currency = TEST_CURRENCY[0],
) = LedgerEntryLine.create(
    id,
    from,
    to,
    testMoney(amount = amount, currency = currency),
)

fun testEntry(
    id: LedgerEntryId = TEST_ENTRY_ID[0],
    type: LedgerEntryType = LedgerEntryType.Expense,
    createdAt: Instant = TEST_INSTANT[0],
    effectiveAt: Instant = TEST_INSTANT[0],
    groupId: GroupId = TEST_GROUP_ID[0],
    groupName: GroupName = TEST_GROUP_NAME[0],
    groupCurrency: Currency = TEST_CURRENCY[0],
    groupUsers: NonEmptySet<UserId> = NonEmptySet.of(TEST_USER_ID[0], TEST_USER_ID[1]),
    lines: NonEmptyList<LedgerEntryLine> = NonEmptyList.of(
        LedgerEntryLine.create(
            TEST_LINE_ID[0],
            TEST_USER_ID[0],
            TEST_USER_ID[1],
            testMoney(currency = TEST_CURRENCY[0])
        ).getOrElse { fail("Invalid test LedgerEntryLine: $it") }
    ),
    creatorId: UserId = TEST_USER_ID[0],
): Either<LedgerEntry.CreateError, LedgerEntry> {
    return LedgerEntry.create(
        id,
        type,
        Group(
            groupId,
            groupName,
            groupCurrency,
            groupUsers
        ),
        creatorId,
        createdAt,
        effectiveAt,
        lines,
    )
}

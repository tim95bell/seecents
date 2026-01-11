package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.testutil.TEST_CURRENCY
import com.tim95bell.seecents.testutil.TEST_MONEY_AMOUNT
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRight
import com.tim95bell.seecents.testutil.testMoney
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MoneyAmountTest {
    @Nested
    inner class Create {
        @Test
        fun `can create valid money amount`() {
            testMoney()
        }
    }

    @Nested
    inner class Combine {
        @Test
        fun `can combine money amounts of the same currency`() {
            testMoney(currency = TEST_CURRENCY[0], amount = 1).combine(
                testMoney(
                    currency = TEST_CURRENCY[0],
                    amount = 2
                )
            ) { a, b ->
                a + b
            }.assertRight().value.let {
                assertEquals(TEST_CURRENCY[0], it.currency)
                assertEquals(3, it.amount)
            }
        }

        @Test
        fun `can NOT combine money amounts of different currencies`() {
            testMoney(currency = TEST_CURRENCY[0], amount = TEST_MONEY_AMOUNT[0])
                .combine(testMoney(currency = TEST_CURRENCY[1], amount = TEST_MONEY_AMOUNT[1])) { a, b ->
                    a + b
                }.assertLeftEq(MoneyAmount.CombineError.CombineDifferingCurrenciesError)
        }
    }
}

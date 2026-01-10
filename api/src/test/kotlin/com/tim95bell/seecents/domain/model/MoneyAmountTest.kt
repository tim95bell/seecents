package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.testutil.AUD
import com.tim95bell.seecents.testutil.EUR
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRight
import com.tim95bell.seecents.testutil.testMoney
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MoneyAmountTest {
    @Test
    fun `can create valid money amount`() {
        testMoney()
    }

    @Test
    fun `can combine money amounts of the same currency`() {
        testMoney(currency = AUD, amount = 1).combine(testMoney(currency = AUD, amount = 2)) {
            a, b -> a + b
        }.assertRight().value.let {
            assertEquals(AUD, it.currency)
            assertEquals(3, it.amount)
        }
    }

    @Test
    fun `can NOT combine money amounts of different currencies`() {
        testMoney(currency = AUD, amount = 1).combine(testMoney(currency = EUR, amount = 2)) {
                a, b -> a + b
        }.assertLeftEq(MoneyAmount.CombineError.CombineDifferingCurrenciesError)
    }
}

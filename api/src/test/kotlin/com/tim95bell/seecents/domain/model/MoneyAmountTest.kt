package com.tim95bell.seecents.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MoneyAmountTest {
    @Test
    fun `can create valid money amount`() {
        money()
    }

    @Test
    fun `can combine money amounts of the same currency`() {
        val result = money(currency = AUD, amount = 1).combine(money(currency = AUD, amount = 2)) {
            a, b -> a + b
        }
        assertEquals(AUD, result.currency)
        assertEquals(3, result.amount)
    }

    @Test
    fun `can NOT combine money amounts of different currencies`() {
        assertThrows(IllegalArgumentException::class.java) {
            money(currency = AUD, amount = 1).combine(money(currency = EUR, amount = 2)) {
                    a, b -> a + b
            }
        }
    }
}

package com.tim95bell.seecents.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LedgerEntryLineTest {
    @Test
    fun `can create valid line`() {
        line()
    }

    @Test
    fun `line fromUserId can equal toUserId`() {
        line(from = 1, to = 1)
    }

    @Test
    fun `line fromUserId can NOT equal toUserId`() {
        line(from = 1, to = 2)
    }

    @Test
    fun `line amount can be positive non zero`() {
        line(amount = 1L)
    }

    @Test
    fun `line amount can NOT be zero`() {
        assertThrows(IllegalArgumentException::class.java) {
            line(amount = 0L)
        }
    }

    @Test
    fun `line amount can NOT be negative non zero`() {
        assertThrows(IllegalArgumentException::class.java) {
            line(amount = -10L)
        }
    }
}

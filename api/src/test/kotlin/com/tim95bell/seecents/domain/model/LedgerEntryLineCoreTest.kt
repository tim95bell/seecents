package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*
import org.junit.jupiter.api.Test

class LedgerEntryLineCoreTest {
    @Test
    fun `can create valid line`() {
        testLine().assertOk()
    }

    @Test
    fun `line fromUserId can equal toUserId`() {
        testLine(from = 1, to = 1).assertOk()
    }

    @Test
    fun `line fromUserId can NOT equal toUserId`() {
        testLine(from = 1, to = 2).assertOk()
    }

    @Test
    fun `line amount can be positive non zero`() {
        testLine(amount = 1L).assertOk()
    }

    @Test
    fun `line amount can NOT be zero`() {
        testLine(amount = 0L).assertErrorEq(LedgerEntryLineCore.CreateError.NonPositiveAmount)
    }

    @Test
    fun `line amount can NOT be negative non zero`() {
        testLine(amount = -10L).assertErrorEq(LedgerEntryLineCore.CreateError.NonPositiveAmount)
    }
}

package com.tim95bell.seecents.domain.model

import org.junit.jupiter.api.Test

class LedgerEntryLineCoreTest {
    @Test
    fun `can create valid line`() {
        testLine().assertRight()
    }

    @Test
    fun `line fromUserId can equal toUserId`() {
        testLine(from = 1, to = 1).assertRight()
    }

    @Test
    fun `line fromUserId can NOT equal toUserId`() {
        testLine(from = 1, to = 2).assertRight()
    }

    @Test
    fun `line amount can be positive non zero`() {
        testLine(amount = 1L).assertRight()
    }

    @Test
    fun `line amount can NOT be zero`() {
        testLine(amount = 0L).assertLeftEq(LedgerEntryLineCore.CreateError.NonPositiveAmount)
    }

    @Test
    fun `line amount can NOT be negative non zero`() {
        testLine(amount = -10L).assertLeftEq(LedgerEntryLineCore.CreateError.NonPositiveAmount)
    }
}

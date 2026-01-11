package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.testutil.TEST_USER_ID
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRight
import com.tim95bell.seecents.testutil.testLine
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LedgerEntryLineTest {
    @Nested
    inner class Create {
        @Test
        fun `can create valid line`() {
            testLine().assertRight()
        }

        @Test
        fun `line fromUserId can equal toUserId`() {
            testLine(from = TEST_USER_ID[0], to = TEST_USER_ID[0]).assertRight()
        }

        @Test
        fun `line fromUserId can NOT equal toUserId`() {
            testLine(from = TEST_USER_ID[0], to = TEST_USER_ID[1]).assertRight()
        }

        @Test
        fun `line amount can be positive non zero`() {
            testLine(amount = 1L).assertRight()
        }

        @Test
        fun `line amount can NOT be zero`() {
            testLine(amount = 0L).assertLeftEq(LedgerEntryLine.CreateError.NonPositiveAmount)
        }

        @Test
        fun `line amount can NOT be negative non zero`() {
            testLine(amount = -10L).assertLeftEq(LedgerEntryLine.CreateError.NonPositiveAmount)
        }
    }
}

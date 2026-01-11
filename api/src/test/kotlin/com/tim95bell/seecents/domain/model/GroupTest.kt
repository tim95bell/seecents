package com.tim95bell.seecents.domain.model

import arrow.core.NonEmptySet
import com.tim95bell.seecents.testutil.TEST_CURRENCY
import com.tim95bell.seecents.testutil.TEST_GROUP_ID
import com.tim95bell.seecents.testutil.TEST_GROUP_NAME
import com.tim95bell.seecents.testutil.TEST_USER_ID
import com.tim95bell.seecents.testutil.assertLeft
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRight
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GroupTest {
    @Nested
    inner class AddUser {
        @Test
        fun `can add user to group when they are not a member and are invited by a member`() {
            val invitingUser = TEST_USER_ID[0]
            val invitedUser = TEST_USER_ID[1]
            val users = NonEmptySet.of(invitingUser)
            val name = TEST_GROUP_NAME[0]
            val group = Group(TEST_GROUP_ID[0], name, TEST_CURRENCY[0], users)
            group.addUser(invitingUser, invitedUser).assertRight().value.let {
                assertEquals(group.users.size + 1, it.users.size)
                assertTrue(it.users.contains(invitedUser))
                assertEquals(group.users, it.users - invitedUser)
            }
        }

        @Test
        fun `can NOT add user to group when they are a member and are invited by a member`() {
            val invitingUser = TEST_USER_ID[0]
            val invitedUser = TEST_USER_ID[1]
            val users = NonEmptySet.of(invitingUser, invitedUser)
            val name = TEST_GROUP_NAME[0]
            Group(TEST_GROUP_ID[0], name, TEST_CURRENCY[0], users)
                .addUser(invitingUser, invitedUser)
                .assertLeftEq(Group.AddUserError.InvitedUserAlreadyInGroup)
        }

        @Test
        fun `can NOT add user to group when they are not a member and are invited by a non member`() {
            val invitingUser = TEST_USER_ID[0]
            val invitedUser = TEST_USER_ID[1]
            val users = NonEmptySet.of(TEST_USER_ID[2])
            val name = TEST_GROUP_NAME[0]
            Group(TEST_GROUP_ID[0], name, TEST_CURRENCY[0], users)
                .addUser(invitingUser, invitedUser)
                .assertLeftEq(Group.AddUserError.InvitingUserNotInGroup)
        }

        @Test
        fun `can NOT add user to group when they are a member and are invited by a non member`() {
            val invitingUser = TEST_USER_ID[0]
            val invitedUser = TEST_USER_ID[1]
            val users = NonEmptySet.of(invitedUser)
            val name = TEST_GROUP_NAME[0]
            Group(TEST_GROUP_ID[0], name, TEST_CURRENCY[0], users)
                .addUser(invitingUser, invitedUser)
                .assertLeft()
        }
    }
}

package com.tim95bell.seecents.domain.model

import arrow.core.NonEmptySet
import arrow.core.flatMap
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GroupCoreTest {
    @Test
    fun `can add user to group when they are not a member and are invited by a member`() {
        val invitingUser = testUserId(1)
        val invitedUser = testUserId(2)
        val users = NonEmptySet.of(invitingUser)
        val name = GroupName.fromCanonical("test").assertRight().value
        val group = GroupCore(name, AUD, users)
        val result = group.addUser(invitingUser, invitedUser).assertRight().value
        assertEquals(group.users.size + 1, result.users.size)
        assertTrue(result.users.contains(invitedUser))
        assertEquals(group.users, result.users - invitedUser)
    }

    @Test
    fun `can NOT add user to group when they are a member and are invited by a member`() {
        val invitingUser = testUserId(1)
        val invitedUser = testUserId(2)
        val users = NonEmptySet.of(invitingUser, invitedUser)
        val name = GroupName.fromCanonical("test").assertRight().value
        GroupCore(name, AUD, users)
            .addUser(invitingUser, invitedUser)
            .assertLeftEq(GroupCore.AddUserError.InvitedUserAlreadyInGroup)
    }

    @Test
    fun `can NOT add user to group when they are not a member and are invited by a non member`() {
        val invitingUser = testUserId(1)
        val invitedUser = testUserId(2)
        val users = NonEmptySet.of(testUserId(3))
        val name = GroupName.fromCanonical("test").assertRight().value
        GroupCore(name, AUD, users)
            .addUser(invitingUser, invitedUser)
            .assertLeftEq(GroupCore.AddUserError.InvitingUserNotInGroup)
    }

    @Test
    fun `can NOT add user to group when they are a member and are invited by a non member`() {
        val invitingUser = testUserId(1)
        val invitedUser = testUserId(2)
        val users = NonEmptySet.of(invitedUser)
        val name = GroupName.fromCanonical("test").assertRight().value
        GroupCore(name, AUD, users)
            .addUser(invitingUser, invitedUser)
            .assertLeft()
    }
}

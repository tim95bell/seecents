package com.tim95bell.seecents.domain.model

import arrow.core.NonEmptySet
import com.tim95bell.seecents.testutil.AUD
import com.tim95bell.seecents.testutil.U1
import com.tim95bell.seecents.testutil.U2
import com.tim95bell.seecents.testutil.U3
import com.tim95bell.seecents.testutil.assertLeft
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRight
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GroupTest {
    @Test
    fun `can add user to group when they are not a member and are invited by a member`() {
        val invitingUser = U1
        val invitedUser = U2
        val users = NonEmptySet.of(invitingUser)
        val name = GroupName.fromCanonical("test").assertRight().value
        val group = Group(GroupId.new(), name, AUD, users)
        val result = group.addUser(invitingUser, invitedUser).assertRight().value
        assertEquals(group.users.size + 1, result.users.size)
        assertTrue(result.users.contains(invitedUser))
        assertEquals(group.users, result.users - invitedUser)
    }

    @Test
    fun `can NOT add user to group when they are a member and are invited by a member`() {
        val invitingUser = U1
        val invitedUser = U2
        val users = NonEmptySet.of(invitingUser, invitedUser)
        val name = GroupName.fromCanonical("test").assertRight().value
        Group(GroupId.new(), name, AUD, users)
            .addUser(invitingUser, invitedUser)
            .assertLeftEq(Group.AddUserError.InvitedUserAlreadyInGroup)
    }

    @Test
    fun `can NOT add user to group when they are not a member and are invited by a non member`() {
        val invitingUser = U1
        val invitedUser = U2
        val users = NonEmptySet.of(U3)
        val name = GroupName.fromCanonical("test").assertRight().value
        Group(GroupId.new(), name, AUD, users)
            .addUser(invitingUser, invitedUser)
            .assertLeftEq(Group.AddUserError.InvitingUserNotInGroup)
    }

    @Test
    fun `can NOT add user to group when they are a member and are invited by a non member`() {
        val invitingUser = U1
        val invitedUser = U2
        val users = NonEmptySet.of(invitedUser)
        val name = GroupName.fromCanonical("test").assertRight().value
        Group(GroupId.new(), name, AUD, users)
            .addUser(invitingUser, invitedUser)
            .assertLeft()
    }
}

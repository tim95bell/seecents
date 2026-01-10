package com.tim95bell.seecents.domain.model

import arrow.core.flatMap
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GroupCoreTest {
    @Test
    fun `can create a valid group`() {
        val user = testUserId()
        val name = "test"
        GroupCore.create(user, name, AUD).assertRight().value.let {
            assertEquals(name, it.name)
            assertEquals(AUD, it.currency)
            assertEquals(1, it.users.size)
            assertEquals(user, it.users.first())
        }
    }

    @Test
    fun `can create a valid group with name that is surrounded by whitespace, whitespace will be removed`() {
        val user = testUserId()
        val name = "   \n\ttest\t\n "
        GroupCore.create(user, name, AUD).assertRight().value.let {
            assertEquals("test", it.name)
            assertEquals(AUD, it.currency)
            assertEquals(1, it.users.size)
            assertEquals(user, it.users.first())
        }
    }

    @Test
    fun `can NOT create a group with an empty name`() {
        val user = testUserId()
        val name = ""
        GroupCore.create(user, name, AUD).assertLeftEq(
            GroupCore.CreateError.EmptyName,
        )
    }

    @Test
    fun `can NOT create a group with a white space name`() {
        val user = testUserId()
        val name = " \t\n "
        GroupCore.create(user, name, AUD).assertLeftEq(
            GroupCore.CreateError.EmptyName,
        )
    }

    @Test
    fun `can add user to group when they are not a member and are invited by a member`() {
        val invitingUser = testUserId(1)
        val invitedUser = testUserId(2)
        val group = GroupCore.create(invitingUser, "test", AUD).assertRight().value
        val result = group.addUser(invitingUser, invitedUser).assertRight().value
        assertEquals(group.users.size + 1, result.users.size)
        assertTrue(result.users.contains(invitedUser))
        assertEquals(group.users, result.users - invitedUser)
    }

    @Test
    fun `can NOT add user to group when they are a member and are invited by a member`() {
        val invitingUser = testUserId(1)
        val invitedUser = testUserId(2)
        GroupCore.create(setOf(invitingUser, invitedUser), "test", AUD)
            .assertRight()
            .flatMap {
                it.addUser(invitingUser, invitedUser)
            }.assertLeftEq(GroupCore.AddUserError.InvitedUserAlreadyInGroup)
    }

    @Test
    fun `can NOT add user to group when they are not a member and are invited by a non member`() {
        val invitingUser = testUserId(1)
        val invitedUser = testUserId(2)
        val group = GroupCore.create(testUserId(3), "test", AUD).assertRight().value
        group.addUser(invitingUser, invitedUser).assertLeftEq(GroupCore.AddUserError.InvitingUserNotInGroup)
    }

    @Test
    fun `can NOT add user to group when they are a member and are invited by a non member`() {
        val invitingUser = testUserId(1)
        val invitedUser = testUserId(2)
        val group = GroupCore.create(invitedUser, "test", AUD).assertRight().value
        group.addUser(invitingUser, invitedUser).assertLeft()
    }
}

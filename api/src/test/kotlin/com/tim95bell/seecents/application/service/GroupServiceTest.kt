package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.common.fp.*
import com.tim95bell.seecents.domain.model.AUD
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
import com.tim95bell.seecents.domain.model.testGroup
import com.tim95bell.seecents.domain.model.testUserId
import com.tim95bell.seecents.domain.repository.GroupRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GroupServiceTest {
    private lateinit var groupRepo: GroupRepository
    private lateinit var service: GroupService

    @BeforeEach
    fun setup() {
        groupRepo = mockk()
        service = GroupService(groupRepo)
    }

    private fun stubSave() {
        every { groupRepo.save(any()) } returns mockk<Group>()
    }

    private fun stubUpdate() {
        every { groupRepo.update(any()) } returns mockk<Group>()
    }

    private fun stubGetById(group: Group) {
        every { groupRepo.getById(group.id) } returns group
    }

    @Nested
    inner class CreateGroup {
        @Test
        fun `succeeds for valid group`() {
            stubSave()
            service.createGroup(
                testUserId(),
                "test",
                AUD,
            ).assertOk()
        }

        @Test
        fun `fails for group with white space name`() {
            service.createGroup(
                testUserId(),
                "  \t\n  ",
                AUD,
            ).assertErrorEq(GroupService.CreateGroupError.CoreError(GroupCore.CreateError.EmptyName))
        }

        @Test
        fun `fails for group with empty name`() {
            service.createGroup(
                testUserId(),
                "",
                AUD,
            ).assertErrorEq(GroupService.CreateGroupError.CoreError(GroupCore.CreateError.EmptyName))
        }
    }

    @Nested
    inner class AddUserToGroup {
        @Test
        fun `succeeds with inviting user in group and invited user not in group`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = setOf(invitingUser))
            stubUpdate()
            stubGetById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertOk()
        }

        @Test
        fun `fails when group not found`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = setOf(invitingUser))
            stubGetById(group)
            every { groupRepo.getById(group.id) } returns null
            val error = service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertError().error
            assertEquals(GroupService.AddUserToGroupError.GroupNotFound(group.id), error)
        }

        @Test
        fun `fails with inviting user in group and invited user in group`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = setOf(invitingUser, invitedUser))
            stubGetById(group)
            val error = service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertError().error
            assertEquals(GroupService.AddUserToGroupError.CoreError(GroupCore.AddUserError.InvitedUserAlreadyInGroup), error)
        }

        @Test
        fun `fails with inviting user not in group and invited user in group`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = setOf(invitedUser))
            stubGetById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertError()
        }

        @Test
        fun `fails with inviting user not in group and invited user not in group`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = setOf(testUserId(3)))
            stubSave()
            stubGetById(group)
            val error = service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertError().error
            assertEquals(GroupService.AddUserToGroupError.CoreError(GroupCore.AddUserError.InvitingUserNotInGroup), error)
        }
    }
}

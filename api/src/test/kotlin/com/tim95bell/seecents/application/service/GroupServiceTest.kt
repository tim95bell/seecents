package com.tim95bell.seecents.application.service

import arrow.core.NonEmptySet
import com.tim95bell.seecents.testutil.AUD
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
import com.tim95bell.seecents.testutil.assertLeft
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRight
import com.tim95bell.seecents.testutil.testGroup
import com.tim95bell.seecents.testutil.testUserId
import com.tim95bell.seecents.domain.repository.GroupRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
        every { groupRepo.findById(group.id) } returns group
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
            ).assertRight()
        }

        @Test
        fun `fails for group with white space name`() {
            service.createGroup(
                testUserId(),
                "  \t\n  ",
                AUD,
            ).assertLeftEq(GroupService.CreateGroupError.InvalidName)
        }

        @Test
        fun `fails for group with empty name`() {
            service.createGroup(
                testUserId(),
                "",
                AUD,
            ).assertLeftEq(GroupService.CreateGroupError.InvalidName)
        }
    }

    @Nested
    inner class AddUserToGroup {
        @Test
        fun `succeeds with inviting user in group and invited user not in group`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = NonEmptySet.of(invitingUser))
            stubUpdate()
            stubGetById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertRight()
        }

        @Test
        fun `fails when group not found`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = NonEmptySet.of(invitingUser))
            stubGetById(group)
            every { groupRepo.findById(group.id) } returns null
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeftEq(GroupService.AddUserToGroupError.GroupNotFound(group.id))
        }

        @Test
        fun `fails with inviting user in group and invited user in group`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = NonEmptySet.of(invitingUser, invitedUser))
            stubGetById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeftEq(GroupService.AddUserToGroupError.CoreError(GroupCore.AddUserError.InvitedUserAlreadyInGroup))
        }

        @Test
        fun `fails with inviting user not in group and invited user in group`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = NonEmptySet.of(invitedUser))
            stubGetById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeft()
        }

        @Test
        fun `fails with inviting user not in group and invited user not in group`() {
            val invitingUser = testUserId(1)
            val invitedUser = testUserId(2)
            val group = testGroup(users = NonEmptySet.of(testUserId(3)))
            stubSave()
            stubGetById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeftEq(GroupService.AddUserToGroupError.CoreError(GroupCore.AddUserError.InvitingUserNotInGroup))
        }
    }
}

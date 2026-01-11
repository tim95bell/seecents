package com.tim95bell.seecents.application.service

import arrow.core.NonEmptySet
import com.tim95bell.seecents.testutil.AUD
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.testutil.assertLeft
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRight
import com.tim95bell.seecents.testutil.testGroup
import com.tim95bell.seecents.domain.repository.GroupRepository
import com.tim95bell.seecents.testutil.U1
import com.tim95bell.seecents.testutil.U2
import com.tim95bell.seecents.testutil.U3
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

    private fun stubGetById(group: Group) {
        every { groupRepo.findById(group.id) } returns group
    }

    @Nested
    inner class CreateGroup {
        @Test
        fun `succeeds for valid group`() {
            stubSave()
            service.createGroup(
                U1,
                "test",
                AUD,
            ).assertRight()
        }

        @Test
        fun `fails for group with white space name`() {
            service.createGroup(
                U1,
                "  \t\n  ",
                AUD,
            ).assertLeftEq(GroupService.CreateGroupError.InvalidName)
        }

        @Test
        fun `fails for group with empty name`() {
            service.createGroup(
                U1,
                "",
                AUD,
            ).assertLeftEq(GroupService.CreateGroupError.InvalidName)
        }
    }

    @Nested
    inner class AddUserToGroup {
        @Test
        fun `succeeds with inviting user in group and invited user not in group`() {
            val invitingUser = U1
            val invitedUser = U2
            val group = testGroup(users = NonEmptySet.of(invitingUser))
            stubSave()
            stubGetById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertRight()
        }

        @Test
        fun `fails when group not found`() {
            val invitingUser = U1
            val invitedUser = U2
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
            val invitingUser = U1
            val invitedUser = U2
            val group = testGroup(users = NonEmptySet.of(invitingUser, invitedUser))
            stubGetById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeftEq(GroupService.AddUserToGroupError.CoreError(Group.AddUserError.InvitedUserAlreadyInGroup))
        }

        @Test
        fun `fails with inviting user not in group and invited user in group`() {
            val invitingUser = U1
            val invitedUser = U2
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
            val invitingUser = U1
            val invitedUser = U2
            val group = testGroup(users = NonEmptySet.of(U3))
            stubSave()
            stubGetById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeftEq(GroupService.AddUserToGroupError.CoreError(Group.AddUserError.InvitingUserNotInGroup))
        }
    }
}

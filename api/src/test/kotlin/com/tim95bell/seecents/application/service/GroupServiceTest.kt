package com.tim95bell.seecents.application.service

import arrow.core.NonEmptySet
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.testutil.assertLeft
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRight
import com.tim95bell.seecents.testutil.testGroup
import com.tim95bell.seecents.domain.repository.GroupRepository
import com.tim95bell.seecents.testutil.TEST_CURRENCY
import com.tim95bell.seecents.testutil.TEST_USER_ID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

    private fun stubFindById(group: Group) {
        every { groupRepo.findById(group.id) } returns group
    }

    @Nested
    inner class CreateGroup {
        @Test
        fun `succeeds for valid group`() {
            stubSave()
            service.createGroup(
                TEST_USER_ID[0],
                "test",
                TEST_CURRENCY[0],
            ).assertRight()
            verify(exactly = 1) { groupRepo.save(any()) }
        }

        @Test
        fun `fails for group with white space name`() {
            service.createGroup(
                TEST_USER_ID[0],
                "  \t\n  ",
                TEST_CURRENCY[0],
            ).assertLeftEq(GroupService.CreateGroupError.InvalidName)
        }

        @Test
        fun `fails for group with empty name`() {
            service.createGroup(
                TEST_USER_ID[0],
                "",
                TEST_CURRENCY[0],
            ).assertLeftEq(GroupService.CreateGroupError.InvalidName)
        }
    }

    @Nested
    inner class AddUserToGroup {
        @Test
        fun `succeeds with inviting user in group and invited user not in group`() {
            val invitingUser = TEST_USER_ID[0]
            val invitedUser = TEST_USER_ID[1]
            val group = testGroup(users = NonEmptySet.of(invitingUser))
            stubSave()
            stubFindById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertRight()
            verify(exactly = 1) { groupRepo.save(any()) }
        }

        @Test
        fun `fails when group not found`() {
            val invitingUser = TEST_USER_ID[0]
            val invitedUser = TEST_USER_ID[1]
            val group = testGroup(users = NonEmptySet.of(invitingUser))
            stubFindById(group)
            every { groupRepo.findById(group.id) } returns null
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeftEq(GroupService.AddUserToGroupError.GroupNotFound(group.id))
            verify(exactly = 0) { groupRepo.save(any()) }
        }

        @Test
        fun `fails with inviting user in group and invited user in group`() {
            val invitingUser = TEST_USER_ID[0]
            val invitedUser = TEST_USER_ID[1]
            val group = testGroup(users = NonEmptySet.of(invitingUser, invitedUser))
            stubFindById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeftEq(GroupService.AddUserToGroupError.GroupError(Group.AddUserError.InvitedUserAlreadyInGroup))
            verify(exactly = 0) { groupRepo.save(any()) }
        }

        @Test
        fun `fails with inviting user not in group and invited user in group`() {
            val invitingUser = TEST_USER_ID[0]
            val invitedUser = TEST_USER_ID[1]
            val group = testGroup(users = NonEmptySet.of(invitedUser))
            stubFindById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeft()
            verify(exactly = 0) { groupRepo.save(any()) }
        }

        @Test
        fun `fails with inviting user not in group and invited user not in group`() {
            val invitingUser = TEST_USER_ID[0]
            val invitedUser = TEST_USER_ID[1]
            val group = testGroup(users = NonEmptySet.of(TEST_USER_ID[2]))
            stubSave()
            stubFindById(group)
            service.addUserToGroup(
                invitingUser,
                invitedUser,
                group.id,
            ).assertLeftEq(GroupService.AddUserToGroupError.GroupError(Group.AddUserError.InvitingUserNotInGroup))
            verify(exactly = 0) { groupRepo.save(any()) }
        }
    }
}

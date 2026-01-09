package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.common.fp.assertErrorEq
import com.tim95bell.seecents.common.fp.assertOk
import com.tim95bell.seecents.domain.model.AUD
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
import com.tim95bell.seecents.domain.model.testUserId
import com.tim95bell.seecents.domain.repository.GroupRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GroupServiceTest {
    private lateinit var groupRepo: GroupRepository
    private lateinit var service: GroupService

    @BeforeEach
    fun setup() {
        groupRepo = mockk()
        service = GroupService(groupRepo)
    }

    private fun stubSaveGroup() {
        every { groupRepo.save(any()) } returns mockk<Group>()
    }

    @Test
    fun `succeeds for valid group`() {
        stubSaveGroup()
        service.createGroup(
            testUserId(),
            "test",
            AUD,
        ).assertOk()
    }

    @Test
    fun `fails for group with white space name`() {
        stubSaveGroup()
        service.createGroup(
            testUserId(),
            "  \t\n  ",
            AUD,
        ).assertErrorEq(GroupService.CreateGroupError.CoreError(GroupCore.CreateError.EmptyName))
    }

    @Test
    fun `fails for group with empty name`() {
        stubSaveGroup()
        service.createGroup(
            testUserId(),
            "",
            AUD,
        ).assertErrorEq(GroupService.CreateGroupError.CoreError(GroupCore.CreateError.EmptyName))
    }
}

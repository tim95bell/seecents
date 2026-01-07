package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.assertErrorEq
import com.tim95bell.seecents.common.fp.assertOk
import com.tim95bell.seecents.common.fp.tap
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GroupCoreTest {
    @Test
    fun `can create a valid group`() {
        val user = testUserId()
        val name = "test"
        GroupCore.create(user, name, AUD).assertOk().tap {
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
        GroupCore.create(user, name, AUD).assertOk().tap {
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
        GroupCore.create(user, name, AUD).assertErrorEq(
            GroupCore.CreateError.EmptyName,
        )
    }

    @Test
    fun `can NOT create a group with a white space name`() {
        val user = testUserId()
        val name = " \t\n "
        GroupCore.create(user, name, AUD).assertErrorEq(
            GroupCore.CreateError.EmptyName,
        )
    }
}

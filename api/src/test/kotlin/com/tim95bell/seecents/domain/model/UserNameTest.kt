package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class UserNameTest {
    @Nested
    inner class FromInput {
        @Test
        fun `succeeds for valid user name`() {
            val name = "test"
            val userName = UserName.fromInput(name).assertOk().value.value
            assertEquals(name, userName)
        }

        @Test
        fun `succeeds and normalises for valid user name`() {
            val name = " \n\ttest \n\t"
            val userName = UserName.fromInput(name).assertOk().value.value
            assertEquals("test", userName)
        }

        @Test
        fun `fails for empty user name`() {
            UserName.fromInput("").assertErrorEq(UserName.Error.Invalid)
        }

        @Test
        fun `fails for whitespace user name`() {
            UserName.fromInput(" \n\t ").assertErrorEq(UserName.Error.Invalid)
        }
    }

    inner class FromCanonical {
        @Test
        fun `succeeds for valid user name`() {
            val name = "test"
            val userName = UserName.fromCanonical(name).assertOk().value.value
            assertEquals(name, userName)
        }

        @Test
        fun `fails for valid user name that needs normalisation`() {
            val name = " \n\ttest \n\t"
            UserName.fromCanonical(name).assertErrorEq(UserName.Error.Invalid)
        }

        @Test
        fun `fails for empty user name`() {
            UserName.fromCanonical("").assertErrorEq(UserName.Error.Invalid)
        }

        @Test
        fun `fails for whitespace user name`() {
            UserName.fromCanonical(" \n\t ").assertErrorEq(UserName.Error.Invalid)
        }
    }
}

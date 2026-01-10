package com.tim95bell.seecents.domain.model

import org.junit.jupiter.api.Nested
import kotlin.test.Test

class UserNameTest {
    @Nested
    inner class FromInput {
        @Test
        fun `succeeds for valid user name`() {
            val name = "test"
            UserName.fromInput(name).map { it.value }.assertRightEq(name)
        }

        @Test
        fun `succeeds and normalises for valid user name`() {
            val name = " \n\ttest \n\t"
            UserName.fromInput(name).map { it.value }.assertRightEq("test")
        }

        @Test
        fun `fails for empty user name`() {
            UserName.fromInput("").assertLeftEq(UserName.Error.Invalid)
        }

        @Test
        fun `fails for whitespace user name`() {
            UserName.fromInput(" \n\t ").assertLeftEq(UserName.Error.Invalid)
        }
    }

    @Nested
    inner class FromCanonical {
        @Test
        fun `succeeds for valid user name`() {
            val name = "test"
            UserName.fromCanonical(name).map { it.value }.assertRightEq(name)
        }

        @Test
        fun `fails for valid user name that needs normalisation`() {
            val name = " \n\ttest \n\t"
            UserName.fromCanonical(name).assertLeftEq(UserName.Error.Invalid)
        }

        @Test
        fun `fails for empty user name`() {
            UserName.fromCanonical("").assertLeftEq(UserName.Error.Invalid)
        }

        @Test
        fun `fails for whitespace user name`() {
            UserName.fromCanonical(" \n\t ").assertLeftEq(UserName.Error.Invalid)
        }
    }
}

package com.tim95bell.seecents.domain.model

import org.junit.jupiter.api.Nested
import kotlin.test.Test

class EmailTest {
    @Nested
    inner class FromInput {
        @Test
        fun `succeeds for valid email`() {
            val input = "test@gmail.com"
            Email.fromInput(input).map { it.value }.assertRightEq(input)
        }

        @Test
        fun `succeeds for valid email and normalises`() {
            val input = " \n\tTeSt@gMail.com \n\t"
            Email.fromInput(input).map { it.value }.assertRightEq("test@gmail.com")
        }

        @Test
        fun `fails for invalid email`() {
            val input = "test.com"
            Email.fromInput(input).assertLeftEq(Email.Error.Invalid)
        }
    }

    @Nested
    inner class FromCanonical {
        @Test
        fun `succeeds for valid email`() {
            val input = "test@gmail.com"
            Email.fromCanonical(input).map { it.value }.assertRightEq(input)
        }

        @Test
        fun `fails for valid email that requires normalisation`() {
            val input = " \n\tTeEt@gMail.com \n\t"
            Email.fromCanonical(input).assertLeftEq(Email.Error.Invalid)
        }

        @Test
        fun `fails for invalid email`() {
            val input = "test.com"
            Email.fromCanonical(input).assertLeftEq(Email.Error.Invalid)
        }
    }
}

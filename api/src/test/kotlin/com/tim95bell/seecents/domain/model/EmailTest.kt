package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class EmailTest {
    @Nested
    inner class FromInput {
        @Test
        fun `succeeds for valid email`() {
            val input = "test@gmail.com"
            val email = Email.fromInput(input).assertOk().value.value
            assertEquals(input, email)
        }

        @Test
        fun `succeeds for valid email and normalises`() {
            val input = " \n\tTeSt@gMail.com \n\t"
            val email = Email.fromInput(input).assertOk().value.value
            assertEquals("test@gmail.com", email)
        }

        @Test
        fun `fails for invalid email`() {
            val input = "test.com"
            Email.fromInput(input).assertErrorEq(Email.Error.Invalid)
        }
    }

    @Nested
    inner class FromCanonical {
        @Test
        fun `succeeds for valid email`() {
            val input = "test@gmail.com"
            val email = Email.fromCanonical(input).assertOk().value.value
            assertEquals(input, email)
        }

        @Test
        fun `fails for valid email that requires normalisation`() {
            val input = " \n\tTeEt@gMail.com \n\t"
            Email.fromCanonical(input).assertErrorEq(Email.Error.Invalid)
        }

        @Test
        fun `fails for invalid email`() {
            val input = "test.com"
            Email.fromCanonical(input).assertErrorEq(Email.Error.Invalid)
        }
    }
}

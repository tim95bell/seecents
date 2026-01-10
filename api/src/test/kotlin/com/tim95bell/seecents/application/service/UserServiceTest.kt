package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.common.fp.*
import com.tim95bell.seecents.domain.model.Email
import com.tim95bell.seecents.domain.model.User
import com.tim95bell.seecents.domain.model.UserCore
import com.tim95bell.seecents.domain.model.testUserCore
import com.tim95bell.seecents.domain.model.testUserId
import com.tim95bell.seecents.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class UserServiceTest {
    private lateinit var userRepo: UserRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        userRepo = mockk()
        userService = UserService(userRepo)
    }

    fun stubFindByEmail(user: User) {
        every { userRepo.findByEmail(user.core.email) } returns user
    }

    fun stubFindByEmail(email: Email) {
        every { userRepo.findByEmail(email) } returns null
    }

    fun stubSave(userCore: UserCore, id: Int = 1) {
        every { userRepo.save(userCore) } returns User(testUserId(id), userCore)
    }

    @Nested
    inner class CreateAccount {
        @Test
        fun `succeeds for valid input`() {
            val userCore = testUserCore()
            stubFindByEmail(userCore.email)
            stubSave(userCore)
            val user = userService.createAccount(
                userCore.name.value,
                userCore.email.value,
                userCore.passwordHash,
            ).assertOk().value
            assertEquals(userCore.name, user.core.name)
            assertEquals(userCore.email, user.core.email)
            assertEquals(userCore.passwordHash, user.core.passwordHash)
            verify(exactly = 1) {
                userRepo.findByEmail(userCore.email)
            }
            verify(exactly = 1) {
                userRepo.save(userCore)
            }
        }

        @Test
        fun `fails for valid input where email already exists`() {
            val userCore = testUserCore()
            stubFindByEmail(User(testUserId(), userCore))
            val user = userService.createAccount(
                userCore.name.value,
                userCore.email.value,
                userCore.passwordHash,
            ).assertErrorEq(UserService.CreateAccountError.EmailAlreadyExists)
        }
    }
}

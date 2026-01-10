package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.domain.model.Email
import com.tim95bell.seecents.domain.model.PasswordHash
import com.tim95bell.seecents.domain.model.User
import com.tim95bell.seecents.domain.model.UserCore
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRightEq
import com.tim95bell.seecents.testutil.testUser
import com.tim95bell.seecents.testutil.testUserCore
import com.tim95bell.seecents.testutil.testUserId
import com.tim95bell.seecents.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

    private fun stubFindByEmailNotFound(user: User) {
        every { userRepo.findByEmail(user.core.email) } returns user
    }

    private fun stubFindByEmailNotFound(email: Email) {
        every { userRepo.findByEmail(email) } returns null
    }

    private fun stubSave(userCore: UserCore, id: Int = 1) {
        stubSave(User(testUserId(id), userCore))
    }

    private fun stubSave(user: User) {
        every { userRepo.save(user.core) } returns user
    }

    @Nested
    inner class CreateAccount {
        @Test
        fun `succeeds for valid input`() {
            val userCore = testUserCore()
            stubFindByEmailNotFound(userCore.email)
            stubSave(userCore)
            userService.createAccount(
                userCore.name.value,
                userCore.email.value,
                userCore.passwordHash,
            ).map { it.core }.assertRightEq(userCore)
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
            stubFindByEmailNotFound(User(testUserId(), userCore))
            userService.createAccount(
                userCore.name.value,
                userCore.email.value,
                userCore.passwordHash,
            ).assertLeftEq(UserService.CreateAccountError.EmailAlreadyExists)
        }
    }

    @Nested
    inner class Login {
        @Test
        fun `succeeds for valid input`() {
            val user = testUser()
            stubFindByEmailNotFound(user)
            userService.login(user.core.email.value, user.core.passwordHash)
                .assertRightEq(user)
            verify(exactly = 1) { userRepo.findByEmail(user.core.email) }
        }

        @Test
        fun `fails for user that does not exist`() {
            val user = testUser()
            stubFindByEmailNotFound(user.core.email)
            userService.login(user.core.email.value, user.core.passwordHash)
                .assertLeftEq(UserService.LoginError.Invalid)
            verify(exactly = 1) { userRepo.findByEmail(user.core.email) }
        }

        @Test
        fun `fails for user invalid email`() {
            userService.login("test", PasswordHash("password"))
                .assertLeftEq(UserService.LoginError.Invalid)
            verify(exactly = 0) { userRepo.findByEmail(any()) }
        }

        @Test
        fun `fails for user incorrect password`() {
            val user = testUser()
            stubFindByEmailNotFound(user)
            userService.login(user.core.email.value, PasswordHash("differentPassword"))
                .assertLeftEq(UserService.LoginError.Invalid)
            verify(exactly = 1) { userRepo.findByEmail(user.core.email) }
        }
    }
}

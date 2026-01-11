package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.domain.model.Email
import com.tim95bell.seecents.domain.model.PasswordHash
import com.tim95bell.seecents.domain.model.User
import com.tim95bell.seecents.testutil.assertLeftEq
import com.tim95bell.seecents.testutil.assertRightEq
import com.tim95bell.seecents.testutil.testUser
import com.tim95bell.seecents.domain.repository.UserRepository
import com.tim95bell.seecents.testutil.TEST_EMAIL
import com.tim95bell.seecents.testutil.TEST_PASSWORD_HASH
import com.tim95bell.seecents.testutil.TEST_USER_NAME
import com.tim95bell.seecents.testutil.assertRight
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

    private fun stubFindByEmail(email: Email) {
        every { userRepo.findByEmail(email) } returns mockk<User>()
    }

    private fun stubFindByEmail(user: User) {
        every { userRepo.findByEmail(user.email) } returns user
    }

    private fun stubFindByEmailNotFound(email: Email) {
        every { userRepo.findByEmail(email) } returns null
    }

    @Nested
    inner class CreateAccount {
        @Test
        fun `succeeds for valid input`() {
            val name = TEST_USER_NAME[0]
            val email = TEST_EMAIL[0]
            val passwordHash = TEST_PASSWORD_HASH[0]
            stubFindByEmailNotFound(email)
            every { userRepo.save(any()) } returns mockk<User>()
            userService.createAccount(
                name.value,
                email.value,
                passwordHash,
            ).assertRight()
            verify(exactly = 1) {
                userRepo.save(any())
            }
        }

        @Test
        fun `fails for valid input where email already exists`() {
            val name = TEST_USER_NAME[0]
            val email = TEST_EMAIL[0]
            val passwordHash = TEST_PASSWORD_HASH[0]
            stubFindByEmail(email)
            userService.createAccount(
                name.value,
                email.value,
                passwordHash,
            ).assertLeftEq(UserService.CreateAccountError.EmailAlreadyExists)
            verify(exactly = 0) {
                userRepo.save(any())
            }
        }
    }

    @Nested
    inner class Login {
        @Test
        fun `succeeds for valid input`() {
            val user = testUser()
            stubFindByEmail(user)
            userService.login(user.email.value, user.passwordHash)
                .assertRightEq(user)
            verify(exactly = 1) { userRepo.findByEmail(user.email) }
        }

        @Test
        fun `fails for user that does not exist`() {
            val user = testUser()
            stubFindByEmailNotFound(user.email)
            userService.login(user.email.value, user.passwordHash)
                .assertLeftEq(UserService.LoginError.Invalid)
            verify(exactly = 1) { userRepo.findByEmail(user.email) }
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
            stubFindByEmail(user)
            userService.login(user.email.value, PasswordHash("differentPassword"))
                .assertLeftEq(UserService.LoginError.Invalid)
            verify(exactly = 1) { userRepo.findByEmail(user.email) }
        }
    }
}

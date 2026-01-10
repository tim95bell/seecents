package com.tim95bell.seecents.application.service

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.tim95bell.seecents.domain.model.Email
import com.tim95bell.seecents.domain.model.PasswordHash
import com.tim95bell.seecents.domain.model.User
import com.tim95bell.seecents.domain.model.UserCore
import com.tim95bell.seecents.domain.model.UserName
import com.tim95bell.seecents.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepo: UserRepository,
) {
    sealed interface CreateAccountError {
        data object InvalidEmail : CreateAccountError
        data object InvalidName : CreateAccountError
        data object EmailAlreadyExists : CreateAccountError
    }

    fun createAccount(
        name: String,
        email: String,
        passwordHash: PasswordHash
    ): Either<CreateAccountError, User> = either {
        val email = Email.fromInput(email)
            .mapLeft { CreateAccountError.InvalidEmail }
            .bind()

        val name = UserName.fromInput(name)
            .mapLeft { CreateAccountError.InvalidName }
            .bind()

        if (userRepo.findByEmail(email) != null) {
            raise(CreateAccountError.EmailAlreadyExists)
        } else {
            userRepo.save(UserCore(name, email, passwordHash))
        }
    }

    sealed interface LoginError {
        data object Invalid : LoginError
    }

    fun login(email: String, passwordHash: PasswordHash): Either<LoginError, User> = either {
        val email = Email.fromInput(email)
            .mapLeft { LoginError.Invalid }
            .bind()

        val user = userRepo.findByEmail(email) ?: raise(LoginError.Invalid)

        if (user.core.passwordHash != passwordHash) {
            raise(LoginError.Invalid)
        }

        user
    }
}

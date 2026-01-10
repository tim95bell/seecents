package com.tim95bell.seecents.application.service

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
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
    ): Either<CreateAccountError, User> {
        return Email.fromInput(email)
            .mapLeft { CreateAccountError.InvalidEmail }
            .flatMap { email ->
                UserName.fromInput(name)
                    .mapLeft { CreateAccountError.InvalidName }
                    .flatMap { name ->
                        if (userRepo.findByEmail(email) != null) {
                            CreateAccountError.EmailAlreadyExists.left()
                        } else {
                            userRepo.save(UserCore(name, email, passwordHash)).right()
                        }
                    }
            }
    }
}

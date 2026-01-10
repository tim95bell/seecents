package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.common.fp.*
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
    ): Result<CreateAccountError, User> {
        return Email.fromInput(email)
            .mapError { CreateAccountError.InvalidEmail }
            .flatMap { email ->
                UserName.fromInput(name)
                    .mapError { CreateAccountError.InvalidName }
                    .flatMap { name ->
                        if (userRepo.findByEmail(email) != null) {
                            error(CreateAccountError.EmailAlreadyExists)
                        } else {
                            ok(userRepo.save(UserCore(name, email, passwordHash)))
                        }
                    }
            }
    }
}

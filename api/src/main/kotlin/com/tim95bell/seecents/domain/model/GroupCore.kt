package com.tim95bell.seecents.domain.model

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.Currency

@ConsistentCopyVisibility
data class GroupCore private constructor(
    val name: String,
    val currency: Currency,
    val users: Set<UserId>,
) {
    sealed interface CreateError {
        data object EmptyName : CreateError
    }

    companion object {
        private fun validateName(name: String): String {
            val trimmedName = name.trim()
            require(trimmedName.isNotBlank())

            return trimmedName
        }

        fun create(creator: UserId, name: String, currency: Currency): Either<CreateError, GroupCore> {
            val validatedName = try {
                validateName(name)
            } catch (_: Exception) {
                return CreateError.EmptyName.left()
            }

            return GroupCore(validatedName, currency, setOf(creator)).right()
        }

        fun create(users: Set<UserId>, name: String, currency: Currency): Either<CreateError, GroupCore> {
            val validatedName = try {
                validateName(name)
            } catch (_: Exception) {
                return CreateError.EmptyName.left()
            }

            return GroupCore(validatedName, currency, users).right()
        }
    }

    sealed interface AddUserError {
        data object InvitingUserNotInGroup : AddUserError
        data object InvitedUserAlreadyInGroup : AddUserError
    }

    fun addUser(invitingUser: UserId, invitedUser: UserId): Either<AddUserError, GroupCore> {
        if (!users.contains(invitingUser)) {
            return AddUserError.InvitingUserNotInGroup.left()
        }

        if (users.contains(invitedUser)) {
            return AddUserError.InvitedUserAlreadyInGroup.left()
        }

        return copy(
            users = this.users + invitedUser,
        ).right()
    }
}

package com.tim95bell.seecents.domain.model

import com.tim95bell.seecents.common.fp.*
import java.util.Currency

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

        fun create(creator: UserId, name: String, currency: Currency): Result<CreateError, GroupCore> {
            val validatedName = try {
                validateName(name)
            } catch (e: Exception) {
                return error(CreateError.EmptyName)
            }

            return ok(GroupCore(validatedName, currency, setOf(creator)))
        }

        fun create(users: Set<UserId>, name: String, currency: Currency): Result<CreateError, GroupCore> {
            val validatedName = try {
                validateName(name)
            } catch (e: Exception) {
                return error(CreateError.EmptyName)
            }

            return ok(GroupCore(validatedName, currency, users))
        }
    }

    sealed interface AddUserError {
        data object InvitingUserNotInGroup : AddUserError
        data object InvitedUserAlreadyInGroup : AddUserError
    }

    fun addUser(invitingUser: UserId, invitedUser: UserId): Result<AddUserError, GroupCore> {
        if (!users.contains(invitingUser)) {
            return error(AddUserError.InvitingUserNotInGroup)
        }

        if (users.contains(invitedUser)) {
            return error(AddUserError.InvitedUserAlreadyInGroup)
        }

        return ok(copy(
            users = this.users + invitedUser,
        ))
    }
}

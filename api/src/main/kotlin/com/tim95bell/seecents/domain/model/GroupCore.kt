package com.tim95bell.seecents.domain.model

import arrow.core.Either
import arrow.core.NonEmptySet
import arrow.core.left
import arrow.core.right
import java.util.Currency

data class GroupCore(
    val name: GroupName,
    val currency: Currency,
    val users: NonEmptySet<UserId>,
) {
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

package com.tim95bell.seecents.application.service

import arrow.core.Either
import arrow.core.NonEmptySet
import arrow.core.left
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupId
import com.tim95bell.seecents.domain.model.GroupName
import com.tim95bell.seecents.domain.model.UserId
import com.tim95bell.seecents.domain.repository.GroupRepository
import org.springframework.stereotype.Service
import java.util.Currency

@Service
class GroupService(
    private val groupRepo: GroupRepository,
) {
    sealed interface CreateGroupError {
        data object InvalidName : CreateGroupError
    }

    fun createGroup(creator: UserId, name: String, currency: Currency): Either<CreateGroupError, Group> {
        return GroupName.fromInput(name)
            .mapLeft { CreateGroupError.InvalidName }
            .map { name ->
                Group(GroupId.new(), name, currency, NonEmptySet.of(creator))
            }
            .map(groupRepo::save)
    }

    sealed interface AddUserToGroupError {
        data class GroupNotFound(val groupId: GroupId) : AddUserToGroupError
        data class GroupError(val groupError: Group.AddUserError) : AddUserToGroupError
    }

    fun addUserToGroup(invitingUser: UserId, invitedUser: UserId, groupId: GroupId): Either<AddUserToGroupError, Group> {
        val group = groupRepo.findById(groupId) ?: return AddUserToGroupError.GroupNotFound(groupId).left()

        return group.addUser(invitingUser, invitedUser)
            .mapLeft(AddUserToGroupError::GroupError)
            .map {
                groupRepo.save(group)
            }
    }
}

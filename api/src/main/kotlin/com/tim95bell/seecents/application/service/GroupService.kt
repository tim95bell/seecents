package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.common.fp.*
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
import com.tim95bell.seecents.domain.model.GroupId
import com.tim95bell.seecents.domain.model.UserId
import com.tim95bell.seecents.domain.repository.GroupRepository
import org.springframework.stereotype.Service
import java.util.Currency

@Service
class GroupService(
    private val groupRepository: GroupRepository,
) {
    sealed interface CreateGroupError {
        data class CoreError(val coreError: GroupCore.CreateError) : CreateGroupError
    }

    fun createGroup(creator: UserId, name: String, currency: Currency): Result<CreateGroupError, Group> {
        return GroupCore.create(creator, name, currency)
            .mapError(CreateGroupError::CoreError)
            .map(groupRepository::save)
    }

    sealed interface AddUserToGroupError {
        data class GroupNotFound(val groupId: GroupId) : AddUserToGroupError
        data class CoreError(val coreError: GroupCore.AddUserError) : AddUserToGroupError
    }

    fun addUserToGroup(invitingUser: UserId, invitedUser: UserId, groupId: GroupId): Result<AddUserToGroupError, Group> {
        val group = groupRepository.getById(groupId) ?: return error(AddUserToGroupError.GroupNotFound(groupId))

        return group.core.addUser(invitingUser, invitedUser)
            .mapError(AddUserToGroupError::CoreError)
            .map {
                groupRepository.update(group.copy(core = it))
            }
    }
}

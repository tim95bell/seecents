package com.tim95bell.seecents.application.service

import com.tim95bell.seecents.common.fp.Result
import com.tim95bell.seecents.common.fp.map
import com.tim95bell.seecents.common.fp.mapError
import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
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
            .map(groupRepository::saveGroup)
    }
}

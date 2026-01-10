package com.tim95bell.seecents.infrastructure.persistence.jpa.repository

import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
import com.tim95bell.seecents.domain.model.GroupId
import com.tim95bell.seecents.domain.repository.GroupRepository
import org.springframework.stereotype.Component

@Component
class JpaGroupRepository : GroupRepository {
    override fun save(group: GroupCore): Group {
        TODO("Not yet implemented")
    }

    override fun update(group: Group): Group {
        TODO("Not yet implemented")
    }

    override fun findById(id: GroupId): Group? {
        TODO("Not yet implemented")
    }
}

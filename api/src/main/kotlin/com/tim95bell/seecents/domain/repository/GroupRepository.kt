package com.tim95bell.seecents.domain.repository

import com.tim95bell.seecents.domain.model.Group
import com.tim95bell.seecents.domain.model.GroupCore
import com.tim95bell.seecents.domain.model.GroupId

interface GroupRepository {
    fun saveGroup(group: GroupCore): Group
    fun getGroupById(id: GroupId): Group?
}

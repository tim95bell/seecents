package com.tim95bell.seecents.domain.repository

import com.tim95bell.seecents.domain.model.Email
import com.tim95bell.seecents.domain.model.User
import com.tim95bell.seecents.domain.model.UserId

interface UserRepository {
    fun findById(id: UserId): User?
    fun findByEmail(email: Email): User?
    fun save(user: User): User
}

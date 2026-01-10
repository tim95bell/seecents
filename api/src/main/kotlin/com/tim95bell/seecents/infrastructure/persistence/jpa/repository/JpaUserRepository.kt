package com.tim95bell.seecents.infrastructure.persistence.jpa.repository

import com.tim95bell.seecents.domain.model.Email
import com.tim95bell.seecents.domain.model.User
import com.tim95bell.seecents.domain.model.UserCore
import com.tim95bell.seecents.domain.model.UserId
import com.tim95bell.seecents.domain.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class JpaUserRepository : UserRepository {
    override fun findById(id: UserId): User? {
        TODO("Not yet implemented")
    }

    override fun findByEmail(email: Email): User? {
        TODO("Not yet implemented")
    }

    override fun save(user: UserCore): User {
        TODO("Not yet implemented")
    }

    override fun update(user: User): User {
        TODO("Not yet implemented")
    }
}

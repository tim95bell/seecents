package com.tim95bell.seecents.domain.model

data class User(
    val id: UserId,
    val name: UserName,
    val email: Email,
    val passwordHash: PasswordHash,
)

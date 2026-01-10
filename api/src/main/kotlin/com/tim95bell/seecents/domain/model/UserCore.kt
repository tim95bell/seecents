package com.tim95bell.seecents.domain.model

data class UserCore(
    val name: UserName,
    val email: Email,
    val passwordHash: PasswordHash,
)

package com.tim95bell.seecents.domain.model

import java.util.Currency

data class GroupCore(
    val currency: Currency,
    val users: Set<UserId>,
)

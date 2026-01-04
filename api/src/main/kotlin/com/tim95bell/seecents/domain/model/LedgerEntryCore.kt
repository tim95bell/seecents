package com.tim95bell.seecents.domain.model

import java.time.Instant

data class LedgerEntryCore(
    val type: LedgerEntryType,
    val groupId: GroupId,
    val createdAt: Instant,
    val effectiveAt: Instant,
    val lines: List<LedgerEntryLineCore>,
) {
    init {
        require(lines.isNotEmpty())
        require(effectiveAt <= createdAt)
        when (type) {
            LedgerEntryType.Payment -> {
                require(lines.all { it.fromId != it.toId })
            }
            LedgerEntryType.Expense -> {
            }
        }
    }
}

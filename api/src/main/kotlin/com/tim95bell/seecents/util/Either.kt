package com.tim95bell.seecents.util

import arrow.core.Either

fun <L, R> Either<L, R>.unwrap(): R = getOrNull()!!

fun <L, R> Either<L, R>.unwrapLeft(): L = leftOrNull()!!

package com.tim95bell.seecents.common.fp

sealed interface Result<out E, out T> {
    data class Ok<T>(val result: T) : Result<Nothing, T>
    data class Error<E>(val error: E) : Result<E, Nothing>
}

fun <E, T>Result<E, T>.tap(f: (T) -> Unit): Result<E, T> = this.also {
    when (it) {
        is Result.Ok -> {
            f(it.result)
        }
        is Result.Error -> {}
    }
}

fun <E, T>Result<E, T>.tapError(f: (E) -> Unit): Result<E, T> = this.also {
    when (it) {
        is Result.Ok -> {}
        is Result.Error -> {
            f(it.error)
        }
    }
}

fun <E, T, T2>Result<E, T>.map(f: (T) -> T2): Result<E, T2> = when (this) {
    is Result.Ok -> Result.Ok(f(result))
    is Result.Error -> this
}

fun <E, T, E2>Result<E, T>.mapError(f: (E) -> E2): Result<E2, T> = when (this) {
    is Result.Ok -> Result.Ok(result)
    is Result.Error -> Result.Error(f(error))
}

fun <E, T, T2>Result<E, T>.flatMap(f: (T) -> Result<E, T2>): Result<E, T2> = when (this) {
    is Result.Ok -> f(result)
    is Result.Error -> this
}

fun <E, T>List<Result<E, T>>.sequence(): Result<E, List<T>> {
    return firstOrNull { it is Result.Error }?.let { Result.Error(it as Result.Error).error }
        ?: Result.Ok(map { (it as Result.Ok).result })
}

fun <E, T>List<Result<E, T>>.sequenceList(): Result<List<E>, List<T>> {
    val (oks, errors) = this.partition { it is Result.Ok }
    if (errors.isEmpty()) {
        return Result.Ok(oks.map { (it as Result.Ok).result })
    }

    return Result.Error(errors.map { (it as Result.Error).error })
}

fun <E, T>Result<E, T>.assertOk(): Result.Ok<T> {
    if (this is Result.Ok) {
        return this
    }

    throw IllegalStateException("Expected Ok but found Error")
}

fun <E, T>Result<E, T>.assertError(): Result.Error<E> {
    if (this is Result.Error) {
        return this
    }

    throw IllegalStateException("Expected Error but found Ok")
}

inline fun <E, T, reified T2 : T>Result<E, T>.assertOkEq(expected: T2): Result.Ok<T> {
    if (this is Result.Ok) {
        if (this is T2 && expected == this.result) {
            return Result.Ok(this.result)
        }

        throw IllegalStateException("Result assertion failed")
    }

    throw IllegalStateException("Expected Ok but found Error")
}

inline fun <E, T, reified E2 : E>Result<E, T>.assertErrorEq(expected: E2): Result.Error<E2> {
    if (this is Result.Error) {
        if (this.error is E2 && this.error == expected) {
            return Result.Error(this.error)
        }

        throw IllegalStateException("Error assertion failed")
    }

    throw IllegalStateException("Expected Error but found Ok")
}

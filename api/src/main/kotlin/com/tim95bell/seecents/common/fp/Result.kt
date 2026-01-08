package com.tim95bell.seecents.common.fp

sealed interface Result<out E, out T> {
    data class Ok<T>(val value: T) : Result<Nothing, T>
    data class Error<E>(val error: E) : Result<E, Nothing>
}

fun <E>error(error: E) = Result.Error<E>(error)

fun <T>ok(result: T) = Result.Ok<T>(result)

fun <E, T>Result<E, T>.tap(f: (T) -> Unit): Result<E, T> = this.also {
    when (it) {
        is Result.Ok -> {
            f(it.value)
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
    is Result.Ok -> ok(f(value))
    is Result.Error -> this
}

fun <E, T, E2>Result<E, T>.mapError(f: (E) -> E2): Result<E2, T> = when (this) {
    is Result.Ok -> this
    is Result.Error -> error(f(error))
}

fun <E, T, T2>Result<E, T>.flatMap(f: (T) -> Result<E, T2>): Result<E, T2> = when (this) {
    is Result.Ok -> f(value)
    is Result.Error -> this
}

fun <E, T>List<Result<E, T>>.sequence(): Result<E, List<T>> {
    return firstOrNull { it is Result.Error }?.let { it as Result.Error }
        ?: ok(map { (it as Result.Ok).value })
}


fun <E, T>List<Result<E, T>>.sequenceList(): Result<List<E>, List<T>> {
    val (oks, errors) = this.partition { it is Result.Ok }
    if (errors.isEmpty()) {
        return ok(oks.map { (it as Result.Ok).value })
    }

    return error(errors.map { (it as Result.Error).error })
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

inline fun <E, T, reified T2 : T>Result<E, T>.assertOkEq(expected: T2): Result.Ok<T2> {
    if (this is Result.Ok) {
        if (this.value is T2 && expected == this.value) {
            return ok(this.value)
        }

        throw IllegalStateException("Result assertion failed")
    }

    throw IllegalStateException("Expected Ok but found Error")
}

inline fun <E, T, reified E2 : E>Result<E, T>.assertErrorEq(expected: E2): Result.Error<E2> {
    if (this is Result.Error) {
        if (this.error is E2 && this.error == expected) {
            return error(this.error)
        }

        throw IllegalStateException("Error assertion failed")
    }

    throw IllegalStateException("Expected Error but found Ok")
}

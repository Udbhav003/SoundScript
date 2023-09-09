package com.udbhav.blackend.ramayana.data.network.helpers

/**
 * Success event extension function for NetworkResult
 */
suspend fun <T : Any> NetworkResult<T>.onSuccess(
    executable: suspend (T) -> Unit
): NetworkResult<T> = apply {
    if (this is NetworkResult.SUCCESS<T>) {
        executable(payload)
    }
}

/**
 * Error event extension function for NetworkResult
 */
suspend fun <T : Any> NetworkResult<T>.onError(
    executable: suspend (code: Int, message: String?) -> Unit
): NetworkResult<T> = apply {
    if (this is NetworkResult.ERROR<T>) {
        executable(code, message)
    }
}

/**
 * Exception event extension function for NetworkResult
 */
suspend fun <T : Any> NetworkResult<T>.onException(
    executable: suspend (err: Throwable) -> Unit
): NetworkResult<T> = apply {
    if (this is NetworkResult.EXCEPTION<T>) {
        executable(exception)
    }
}
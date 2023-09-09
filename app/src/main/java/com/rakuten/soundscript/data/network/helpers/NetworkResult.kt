package com.udbhav.blackend.ramayana.data.network.helpers

/**
 * Wrapper to classify Retrofit API response
 */
sealed class NetworkResult<T : Any> {
    class LOADING<T : Any>(val isLoadingStarted: Boolean) : NetworkResult<T>()
    class SUCCESS<T : Any>(val payload: T) : NetworkResult<T>()
    class ERROR<T : Any>(val code: Int, val message: String?, val tag: String? = null) : NetworkResult<T>()
    class EXCEPTION<T : Any>(val exception: Throwable) : NetworkResult<T>()
}
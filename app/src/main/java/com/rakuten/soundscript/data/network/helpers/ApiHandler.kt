package com.udbhav.blackend.ramayana.data.network.helpers

import retrofit2.HttpException
import retrofit2.Response

object ApiHandler {

    // TAGS
    private const val TAG_API_ERROR = "API Error"

    /**
     * Returns one of these NetworkResult based on the API response
     * 1. NetworkResult.SUCCESS
     * 2. NetworkResult.ERROR
     * 3. NetworkResult.EXCEPTION
     *
     * @param response Generic Retrofit API response
     */
    fun <T : Any> handleApi(response: Response<T>): NetworkResult<T> {
        return try {
            val body = response.body()
            if (response.isSuccessful && body != null) {
                NetworkResult.SUCCESS(body)
            } else {
                NetworkResult.ERROR(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            NetworkResult.ERROR(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            NetworkResult.EXCEPTION(exception = e)
        }
    }
}

package com.rakuten.soundscript.data.network.retrofit

import com.rakuten.soundscript.data.network.response.ContentDetailResponse
import com.rakuten.soundscript.data.network.response.ContentResponse
import com.udbhav.blackend.ramayana.data.network.helpers.NetworkResult
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/creator/contents/{status}")
    suspend fun getContents(
        @Path("status") status: String
    ): NetworkResult<ContentResponse>

    @GET("/creator/content/{id}")
    suspend fun getContentDetail(
        @Path("id") id: String
    ): NetworkResult<ContentDetailResponse>

}
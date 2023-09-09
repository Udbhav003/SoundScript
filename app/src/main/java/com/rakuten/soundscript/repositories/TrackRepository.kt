package com.rakuten.soundscript.repositories

import com.rakuten.soundscript.data.network.response.ContentDetailResponse
import com.rakuten.soundscript.data.network.response.ContentResponse
import com.udbhav.blackend.ramayana.data.network.helpers.NetworkResult

/**
 * An interface defining the operations for managing tracks.
 */
interface TrackRepository {

    suspend fun getContents(): NetworkResult<ContentResponse>

    suspend fun getContentDetail(id: String): NetworkResult<ContentDetailResponse>
}

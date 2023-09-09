package com.rakuten.soundscript.repositories

import com.rakuten.soundscript.data.models.Track
import com.rakuten.soundscript.data.network.response.ContentDetailResponse
import com.rakuten.soundscript.data.network.response.ContentResponse
import com.rakuten.soundscript.data.network.retrofit.ApiService
import com.udbhav.blackend.ramayana.data.network.helpers.NetworkResult
import javax.inject.Inject

/**
 * A concrete implementation of the [TrackRepository] interface.
 * This class is responsible for managing and providing tracks.
 *
 * @constructor Creates an instance of [TrackRepositoryImpl].
 */
class TrackRepositoryImpl @Inject constructor(
    private val service: ApiService
) : TrackRepository {

    /**
     * Retrieves a list of all tracks in the repository.
     *
     * @return a list of [Track] objects.
     */
    override suspend fun getContents(): NetworkResult<ContentResponse> {
        return service.getContents("DRAFT")
    }

    override suspend fun getContentDetail(id: String): NetworkResult<ContentDetailResponse> {
        return service.getContentDetail(id)
    }
}
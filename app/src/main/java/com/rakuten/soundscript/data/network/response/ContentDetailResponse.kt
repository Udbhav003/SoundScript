package com.rakuten.soundscript.data.network.response

import com.rakuten.soundscript.data.models.TrackDetail

data class ContentDetailResponse(
    val data: TrackDetail,
    val status: String
)
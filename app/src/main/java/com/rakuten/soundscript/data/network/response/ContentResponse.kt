package com.rakuten.soundscript.data.network.response

import com.rakuten.soundscript.data.models.Track

data class ContentResponse(
    val data: List<Track>,
    val status: String
)
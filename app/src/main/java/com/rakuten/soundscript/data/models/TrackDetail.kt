package com.rakuten.soundscript.data.models

data class TrackDetail(
    val _id: String,
    val artist: String?,
    val audio: String,
    val heroImage: String?,
    val images: List<String>?,
    val name: String?,
    val status: String,
    val summary: String?,
    val tags: List<String>?,
    val transcription: String?
)

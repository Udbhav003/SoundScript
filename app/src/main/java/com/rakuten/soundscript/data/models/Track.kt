package com.rakuten.soundscript.data.models

import com.rakuten.soundscript.player.PlayerStates
import com.rakuten.soundscript.player.PlayerStates.STATE_IDLE

data class Track(
    val _id: String,
    val artist: String?,
    val audio: String,
    val heroImage: String?,
    val images: List<String>?,
    val name: String,
    var isSelected: Boolean = false,
    var state: PlayerStates = STATE_IDLE
)

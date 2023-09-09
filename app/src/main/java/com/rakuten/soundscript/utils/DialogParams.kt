package com.rakuten.soundscript.utils

import com.rakuten.soundscript.data.models.Track
import com.rakuten.soundscript.player.PlaybackState
import com.rakuten.soundscript.player.PlayerEvents
import kotlinx.coroutines.flow.StateFlow

sealed class DialogParams {

    class PlayerScreenParams(
        val selectedTrack: Track,
        val playerEvents: PlayerEvents,
        val playbackState: StateFlow<PlaybackState>,
        val amplitudes: List<Int>
    ): DialogParams()

    class TranscriptScreenParams(
        val contentId: String
    ): DialogParams()
}

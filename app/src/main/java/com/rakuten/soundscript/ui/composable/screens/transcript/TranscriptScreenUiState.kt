package com.rakuten.soundscript.ui.composable.screens.transcript

import com.rakuten.soundscript.data.models.TrackDetail
import com.rakuten.soundscript.utils.ErrorState

data class TranscriptScreenUiState (
    val isLoading: Boolean,
    val error: ErrorState,
    val trackDetail: TrackDetail?
){

    companion object{
        val DEFAULT = TranscriptScreenUiState(
            isLoading = false,
            error = ErrorState.DEFAULT,
            trackDetail = null
        )
    }
}
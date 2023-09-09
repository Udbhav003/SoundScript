package com.rakuten.soundscript.ui.composable.screens.home

import com.rakuten.soundscript.data.models.Track
import com.rakuten.soundscript.utils.ErrorState

data class HomeScreenUiState (
    val isLoading: Boolean,
    val error: ErrorState,
    val podcasts: List<Track>?,
    val amplitudes: List<Int>
){

    companion object{
        val DEFAULT = HomeScreenUiState(
            isLoading = false,
            error = ErrorState.DEFAULT,
            podcasts = emptyList(),
            amplitudes = emptyList()
        )
    }
}
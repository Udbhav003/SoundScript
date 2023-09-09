package com.rakuten.soundscript.ui.composable.screens.transcript

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.rakuten.soundscript.player.MyPlayer
import com.rakuten.soundscript.repositories.TrackRepository
import com.rakuten.soundscript.ui.composable.screens.home.HomeScreenUiState
import com.rakuten.soundscript.utils.toMediaItemList
import com.udbhav.blackend.ramayana.data.network.helpers.NetworkResult
import com.udbhav.blackend.ramayana.data.network.helpers.onError
import com.udbhav.blackend.ramayana.data.network.helpers.onException
import com.udbhav.blackend.ramayana.data.network.helpers.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranscriptScreenViewModel @Inject constructor(
    private val trackRepository: TrackRepository
): ViewModel() {

    private var _uiState = MutableStateFlow(TranscriptScreenUiState.DEFAULT)
    val uiState: StateFlow<TranscriptScreenUiState> get() = _uiState

    fun fetchContentDetail(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val response = trackRepository.getContentDetail(id)
            response.onSuccess { payload ->
                _uiState.value = _uiState.value.copy(isLoading = false, trackDetail = payload.data)
            }.onError { code, message ->
                message?.let { Log.d("TranscriptScreenVM", it) }
            }.onException {
                it.message?.let { message -> Log.d("TranscriptScreenVM", message) }
            }
        }
    }

}
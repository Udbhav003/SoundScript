@file:Suppress("EmptyMethod")

package com.rakuten.soundscript.ui.composable.screens.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakuten.soundscript.data.models.Track
import com.rakuten.soundscript.player.MyPlayer
import com.rakuten.soundscript.player.PlaybackState
import com.rakuten.soundscript.player.PlayerEvents
import com.rakuten.soundscript.player.PlayerStates
import com.rakuten.soundscript.player.PlayerStates.STATE_END
import com.rakuten.soundscript.player.PlayerStates.STATE_NEXT_TRACK
import com.rakuten.soundscript.player.PlayerStates.STATE_PLAYING
import com.rakuten.soundscript.repositories.TrackRepository
import com.rakuten.soundscript.utils.collectPlayerState
import com.rakuten.soundscript.utils.launchPlaybackStateJob
import com.rakuten.soundscript.utils.resetTracks
import com.rakuten.soundscript.utils.toMediaItemList
import com.udbhav.blackend.ramayana.data.network.helpers.NetworkResult
import com.udbhav.blackend.ramayana.data.network.helpers.onError
import com.udbhav.blackend.ramayana.data.network.helpers.onException
import com.udbhav.blackend.ramayana.data.network.helpers.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import linc.com.amplituda.Amplituda
import linc.com.amplituda.AmplitudaResult
import linc.com.amplituda.Cache
import linc.com.amplituda.exceptions.AmplitudaException
import linc.com.amplituda.exceptions.io.AmplitudaIOException
import javax.inject.Inject

@Suppress("EmptyMethod")
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
    private val myPlayer: MyPlayer
) : ViewModel(), PlayerEvents {

    private var _uiState = MutableStateFlow(HomeScreenUiState.DEFAULT)
    val uiState: StateFlow<HomeScreenUiState> get() = _uiState

    /**
     * A mutable state list of all tracks.
     */
    private val _tracks = mutableStateListOf<Track>()
    /**
     * An immutable snapshot of the current list of tracks.
     */
    val tracks: List<Track> get() = _tracks

    /**
     * A private Boolean variable to keep track of whether a track is currently being played or not.
     */
    private var isTrackPlay: Boolean = false

    /**
     * A public property backed by mutable state that holds the currently selected [Track].
     * It can only be set within the [HomeViewModel] class.
     */
    var selectedTrack: Track? by mutableStateOf(null)
        private set
    /**
     * A private property backed by mutable state that holds the index of the currently selected track.
     */
    private var selectedTrackIndex: Int by mutableStateOf(-1)

    /**
     * A nullable [Job] instance that represents the ongoing process of updating the playback state.
     */
    private var playbackStateJob: Job? = null

    /**
     * A private [MutableStateFlow] that holds the current [PlaybackState].
     * It is used to emit updates about the playback state to observers.
     */
    private val _playbackState = MutableStateFlow(PlaybackState(0L, 0L))
    /**
     * A public property that exposes the [_playbackState] as an immutable [StateFlow] for observers.
     */
    val playbackState: StateFlow<PlaybackState> get() = _playbackState

    /**
     * A private Boolean variable to keep track of whether the track selection is automatic (i.e., due to the completion of a track) or manual.
     */
    private var isAuto: Boolean = false

    /**
     * Initializes the ViewModel. It populates the list of tracks, sets up the media player,
     * and observes the player state.
     */
    init {
        fetchContents()
    }

    private fun fetchContents() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val response = trackRepository.getContents()
            response.onSuccess { payload ->
                _uiState.value = _uiState.value.copy(isLoading = false, podcasts = payload.data)
                _tracks.addAll(payload.data)
                myPlayer.initPlayer(tracks.toMediaItemList())
                observePlayerState()
            }.onError { code, message ->
                message?.let { Log.d("HomeScreenViewModel", it) }
            }.onException {
                it.message?.let { message -> Log.d("HomeScreenViewModel", message) }
            }
        }
    }

    fun fetchAmplitudesByTrackUrl(context: Context, url: String){
        viewModelScope.launch(Dispatchers.IO){
            val amplituda = Amplituda(context)
            amplituda.processAudio(url, Cache.withParams(Cache.REUSE))[{ result: AmplitudaResult<String?> ->
                _uiState.value = _uiState.value.copy(amplitudes = result.amplitudesAsList())
            }, { exception: AmplitudaException? ->
                if (exception is AmplitudaIOException) {
                    println("IO Exception!")
                }
            }]
        }
    }

    /**
     * Handles track selection.
     *
     * @param index The index of the selected track in the track list.
     */
    private fun onTrackSelected(index: Int) {
        if (selectedTrackIndex == -1) isTrackPlay = true
        if (selectedTrackIndex == -1 || selectedTrackIndex != index) {
            _tracks.resetTracks()
            selectedTrackIndex = index
            setUpTrack()
        }
    }

    private fun setUpTrack() {
        if (!isAuto) myPlayer.setUpTrack(selectedTrackIndex, isTrackPlay)
        isAuto = false
    }

    /**
     * Updates the playback state and launches or cancels the playback state job accordingly.
     *
     * @param state The new player state.
     */
    private fun updateState(state: PlayerStates) {
        if (selectedTrackIndex != -1) {
            isTrackPlay = state == STATE_PLAYING
            _tracks[selectedTrackIndex].state = state
            _tracks[selectedTrackIndex].isSelected = true
            selectedTrack = null
            selectedTrack = tracks[selectedTrackIndex]

            updatePlaybackState(state)
            if (state == STATE_NEXT_TRACK) {
                isAuto = true
                onNextClick()
            }
            if (state == STATE_END) onTrackSelected(0)
        }
    }

    private fun observePlayerState() {
        viewModelScope.collectPlayerState(myPlayer, ::updateState)
    }

    private fun updatePlaybackState(state: PlayerStates) {
        playbackStateJob?.cancel()
        playbackStateJob = viewModelScope.launchPlaybackStateJob(_playbackState, state, myPlayer)
    }

    /**
     * Implementation of [PlayerEvents.onPreviousClick].
     * Changes to the previous track if one exists.
     */
    override fun onPreviousClick() {
        if (selectedTrackIndex > 0) onTrackSelected(selectedTrackIndex - 1)
    }

    /**
     * Implementation of [PlayerEvents.onNextClick].
     * Changes to the next track in the list if one exists.
     */
    override fun onNextClick() {
        if (selectedTrackIndex < tracks.size - 1) onTrackSelected(selectedTrackIndex + 1)
    }

    /**
     * Implementation of [PlayerEvents.onPlayPauseClick].
     * Toggles play/pause state of the current track.
     */
    override fun onPlayPauseClick() {
        myPlayer.playPause()
    }

    /**
     * Implementation of [PlayerEvents.onTrackClick].
     * Selects the clicked track from the track list.
     *
     * @param track The track that was clicked.
     */
    override fun onTrackClick(track: Track) {
        onTrackSelected(tracks.indexOf(track))
    }

    /**
     * Implementation of [PlayerEvents.onSeekBarPositionChanged].
     * Seeks to the specified position in the current track.
     *
     * @param position The position to seek to.
     */
    override fun onSeekBarPositionChanged(position: Long) {
        viewModelScope.launch { myPlayer.seekToPosition(position) }
    }

    /**
     * Cleans up the media player when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        myPlayer.releasePlayer()
    }
}

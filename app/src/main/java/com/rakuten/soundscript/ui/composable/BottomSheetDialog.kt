package com.rakuten.soundscript.ui.composable

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.test.internal.util.LogUtil
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.model.WaveformAlignment
import com.rakuten.soundscript.R
import com.rakuten.soundscript.data.models.Track
import com.rakuten.soundscript.player.PlaybackState
import com.rakuten.soundscript.player.PlayerEvents
import com.rakuten.soundscript.ui.Actions
import com.rakuten.soundscript.ui.composable.common.GradientOverlay
import com.rakuten.soundscript.ui.composable.common.LinearGradient
import com.rakuten.soundscript.ui.composable.screens.player.PlayerScreen
import com.rakuten.soundscript.ui.theme.AppTheme
import com.rakuten.soundscript.ui.theme.Typography
import com.rakuten.soundscript.utils.DialogParams
import com.rakuten.soundscript.utils.formatTime
import com.rakuten.soundscript.utils.noRippleClickable
import kotlinx.coroutines.flow.StateFlow

/**
 * [BottomSheetDialog] is a composable that represents the bottom sheet dialog which contains information about the selected track,
 * a slider to monitor and control track progress, and controls for track playback.
 *
 * @param selectedTrack The [Track] object that is currently selected for playback.
 * @param playerEvents The [PlayerEvents] object which encapsulates all the events associated with the player like play, pause, next, previous.
 * @param playbackState A [StateFlow] object representing the playback state, including current playback position and track duration.
 */
@Composable
fun BottomSheetDialog(
    navigation: NavHostController,
    params: DialogParams,
    hideBottomSheetDialog: () -> Unit
) {
    when (params) {
        is DialogParams.PlayerScreenParams -> {
            PlayerScreen(params) {
                hideBottomSheetDialog()
            }
        }

        else -> {
            // Do Nothing
        }
    }
}

/**
 * [TrackInfo] is a composable that displays the image, name, and artist of a track.
 *
 * @param trackImage The resource ID of the track image.
 * @param trackName The name of the track.
 * @param artistName The name of the artist.
 */
@Composable
fun TrackInfo(
    playbackState: StateFlow<PlaybackState>,
    trackName: String,
    artistName: String,
    heroImage: String,
    bannerImages: List<String>,
    onBackClick: () -> Unit
) {

    val width = Dp(LocalConfiguration.current.screenWidthDp.toFloat())
    val height = width.times(1.33f)

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(MaterialTheme.colorScheme.background)
    ) {

        TrackBannerImage(
            playbackState = playbackState,
            bannerImages = bannerImages,
            heroImage = heroImage,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(AppTheme.dimens.grid_2)
                    .noRippleClickable {
                        onBackClick()
                    },
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "BackButton"
            )
            Text(modifier = Modifier.align(Alignment.Center), text = "Playing Now")
        }

        // Overlay
        GradientOverlay(
            modifier = Modifier.fillMaxSize(),
            gradient = LinearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.background.copy(alpha = 0f),
                    MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    MaterialTheme.colorScheme.background.copy(alpha = 1f)
                ),
                stops = listOf(0f, 0.6f, 1f),
                angle = 270f
            )
        )
    }
    Text(
        text = trackName,
        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    )
    Spacer(modifier = Modifier.height(AppTheme.dimens.grid_1))
    Text(
        text = artistName,
        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
        fontSize = MaterialTheme.typography.bodySmall.fontSize,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    )
}

/**
 * [TrackProgressSlider] is a composable that represents a slider for tracking and controlling the progress of the current track.
 *
 * @param playbackState A [StateFlow] object representing the playback state, including current playback position and track duration.
 * @param onSeekBarPositionChanged A lambda which gets executed when the position of the slider is changed.
 */
@Composable
fun TrackProgressSlider(
    playbackState: StateFlow<PlaybackState>,
    amplitudes: List<Int>,
    onSeekBarPositionChanged: (Long) -> Unit
) {
    val playbackStateValue = playbackState.collectAsState(
        initial = PlaybackState(0L, 0L)
    ).value
    var currentMediaProgress = playbackStateValue.currentPlaybackPosition.toFloat()
    var currentPosTemp by rememberSaveable { mutableStateOf(0f) }

    val brush = Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary))

    AudioWaveform(
        modifier = Modifier.fillMaxWidth(),
        progress = currentMediaProgress.div(playbackStateValue.currentTrackDuration.toFloat()),
        style = Fill,
        waveformAlignment = WaveformAlignment.Center,
        amplitudeType = AmplitudeType.Avg,
        progressBrush = brush,
        waveformBrush = SolidColor(Color.LightGray),
        amplitudes = amplitudes,
        onProgressChange = {
            currentPosTemp = it
        },
        onProgressChangeFinished = {
            currentMediaProgress = currentPosTemp
            currentPosTemp = 0f
            onSeekBarPositionChanged(
                currentMediaProgress.times(playbackStateValue.currentTrackDuration).toInt().toLong()
            )
        }
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppTheme.dimens.grid_3),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = playbackStateValue.currentPlaybackPosition.formatTime(),
            style = Typography.bodySmall
        )
        Text(
            text = playbackStateValue.currentTrackDuration.formatTime(),
            style = Typography.bodySmall
        )
    }
}

/**
 * [TrackControls] is a composable that represents the controls for track playback, including previous, play/pause, and next buttons.
 *
 * @param selectedTrack The [Track] object that is currently selected for playback.
 * @param onPreviousClick A lambda which gets executed when the previous button is clicked.
 * @param onPlayPauseClick A lambda which gets executed when the play/pause button is clicked.
 * @param onNextClick A lambda which gets executed when the next button is clicked.
 */
@Composable
fun TrackControls(
    selectedTrack: Track,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.border(AppTheme.dimens.grid_0_25, MaterialTheme.colorScheme.onBackground, CircleShape)){
            PreviousIcon(onClick = onPreviousClick, isBottomTab = false)
        }
        Box(modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary)) {
            PlayPauseIcon(
                selectedTrack = selectedTrack,
                onClick = onPlayPauseClick,
                isBottomTab = false
            )
        }
        Box(modifier = Modifier.border(AppTheme.dimens.grid_0_25, MaterialTheme.colorScheme.onBackground, CircleShape)) {
            NextIcon(onClick = onNextClick, isBottomTab = false)
        }
    }
}
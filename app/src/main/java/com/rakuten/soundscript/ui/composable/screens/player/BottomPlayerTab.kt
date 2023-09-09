package com.rakuten.soundscript.ui.composable.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rakuten.soundscript.data.models.Track
import com.rakuten.soundscript.player.PlaybackState
import com.rakuten.soundscript.player.PlayerEvents
import com.rakuten.soundscript.ui.UIConstants
import com.rakuten.soundscript.ui.composable.NextIcon
import com.rakuten.soundscript.ui.composable.PlayPauseIcon
import com.rakuten.soundscript.ui.composable.TrackImage
import com.rakuten.soundscript.ui.composable.TrackName
import com.rakuten.soundscript.ui.theme.AppTheme
import com.rakuten.soundscript.ui.theme.Typography
import com.rakuten.soundscript.utils.formatTime
import kotlinx.coroutines.flow.StateFlow

/**
 * [BottomPlayerTab] is a composable that represents the bottom player tab UI in the application.
 * This tab displays the currently selected track information and provides controls for playback.
 *
 * @param selectedTrack The [Track] object that is currently selected for playback.
 * @param playerEvents The [PlayerEvents] object which encapsulates all the events associated with the player like play, pause, next, previous.
 * @param onBottomTabClick A lambda which gets executed when the bottom player tab is clicked.
 */
@Composable
fun BottomPlayerTab(
    modifier: Modifier,
    selectedTrack: Track,
    playerState: StateFlow<PlaybackState>,
    playerEvents: PlayerEvents,
    onBottomTabClick: () -> Unit
) {

    val state by playerState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .background(color = Color.Transparent)
            .clickable(onClick = onBottomTabClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(UIConstants.CORNER_RADIUS_XXL))
                .background(color = MaterialTheme.colorScheme.tertiary)
                .padding(AppTheme.dimens.grid_1),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(!selectedTrack.heroImage.isNullOrEmpty()) {
                TrackImage(
                    trackImageUrl = selectedTrack.heroImage,
                    modifier = Modifier.size(size = 50.dp)
                )
            }

            Column (modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 8.dp)) {
                TrackName(trackName = selectedTrack.name, modifier = Modifier)
                Spacer(modifier = Modifier.height(AppTheme.dimens.grid_0_25))
                Text(
                    text = state.currentPlaybackPosition.formatTime(),
                    style = Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
            PlayPauseIcon(
                selectedTrack = selectedTrack,
                onClick = playerEvents::onPlayPauseClick,
                isBottomTab = true
            )
            NextIcon(onClick = playerEvents::onNextClick, isBottomTab = true)
        }
    }
}
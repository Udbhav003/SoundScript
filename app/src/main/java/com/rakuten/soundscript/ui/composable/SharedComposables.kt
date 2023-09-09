@file:OptIn(ExperimentalGlideComposeApi::class, ExperimentalGlideComposeApi::class)

package com.rakuten.soundscript.ui.composable

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.rakuten.soundscript.R
import com.rakuten.soundscript.data.models.Track
import com.rakuten.soundscript.player.PlaybackState
import com.rakuten.soundscript.player.PlayerStates
import com.rakuten.soundscript.ui.UIConstants
import com.rakuten.soundscript.ui.theme.AppTheme
import com.rakuten.soundscript.ui.theme.md_theme_light_onPrimary
import com.rakuten.soundscript.ui.theme.md_theme_light_onPrimaryContainer
import com.rakuten.soundscript.ui.theme.Typography
import kotlinx.coroutines.flow.StateFlow

/**
 * A composable function that displays an image for a track.
 *
 * @param trackImage The resource identifier for the track image.
 * @param modifier The modifier to be applied to the Image.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackImage(
    trackImageUrl: String,
    modifier: Modifier
) {
    GlideImage(
        model = trackImageUrl,
        contentScale = ContentScale.Crop,
        contentDescription = stringResource(id = R.string.track_image),
        modifier = modifier.clip(shape = RoundedCornerShape(UIConstants.CORNER_RADIUS_XXL))
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrackBannerImage(
    playbackState: StateFlow<PlaybackState>,
    bannerImages: List<String>,
    heroImage: String,
    modifier: Modifier
) {

    val state by playbackState.collectAsState()
    val partitions = bannerImages.size

    if(partitions > 0) {
        var index by remember {
            mutableIntStateOf(0)
        }

        val thresholdDuration = state.currentTrackDuration.div(partitions)
        var currentThresholdDuration by remember {
            mutableLongStateOf(thresholdDuration)
        }

        if (state.currentPlaybackPosition >= currentThresholdDuration) {
            currentThresholdDuration += thresholdDuration
            index++
            if (index == partitions) {
                index = 0
            }
        }

        AnimatedContent(
            index,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 1000)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 500))
            }
        ) { targetState ->
            GlideImage(
                model = bannerImages[targetState],
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.track_image),
                modifier = modifier
            ) { requestBuilder ->
                requestBuilder.transition(
                    DrawableTransitionOptions().crossFade(
                        DrawableCrossFadeFactory.Builder(1000).setCrossFadeEnabled(true).build()
                    )
                )
            }
        }
    } else {
        GlideImage(
            model = heroImage,
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(id = R.string.track_image),
            modifier = modifier
        ) { requestBuilder ->
            requestBuilder.transition(
                DrawableTransitionOptions().crossFade(
                    DrawableCrossFadeFactory.Builder(1000).setCrossFadeEnabled(true).build()
                )
            )
        }
    }

}

/**
 * A composable function that displays the name of a track.
 *
 * @param trackName The name of the track.
 * @param modifier The modifier to be applied to the Text.
 */
@Composable
fun TrackName(trackName: String, modifier: Modifier) {
    Text(
        text = trackName,
        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        color = MaterialTheme.colorScheme.onTertiary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

/**
 * A composable function that displays a "Previous" icon.
 *
 * @param onClick The action to be performed when the icon is clicked.
 * @param isBottomTab A boolean indicating whether the icon is part of the bottom tab.
 */
@Composable
fun PreviousIcon(onClick: () -> Unit, isBottomTab: Boolean) {
    IconButton(onClick = onClick, modifier = Modifier.padding(AppTheme.dimens.grid_1)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_previous),
            contentDescription = stringResource(id = R.string.icon_skip_previous),
            tint = if (isBottomTab) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(AppTheme.dimens.grid_3)
        )
    }
}

/**
 * A composable function that displays a "Play/Pause" icon.
 * Displays a loading spinner when the track is buffering.
 *
 * @param selectedTrack The currently selected track.
 * @param onClick The action to be performed when the icon is clicked.
 * @param isBottomTab A boolean indicating whether the icon is part of the bottom tab.
 */
@Composable
fun PlayPauseIcon(selectedTrack: Track, onClick: () -> Unit, isBottomTab: Boolean) {
    if (selectedTrack.state == PlayerStates.STATE_BUFFERING) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size = AppTheme.dimens.grid_6)
                .padding(all = 9.dp),
            color = if (isBottomTab) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onBackground,
        )
    } else {
        IconButton(onClick = onClick, modifier = if(!isBottomTab) Modifier.padding(AppTheme.dimens.grid_2) else Modifier) {
            Icon(
                painter = painterResource(
                    id = if (selectedTrack.state == PlayerStates.STATE_PLAYING) R.drawable.ic_pause
                    else R.drawable.ic_play
                ),
                contentDescription = stringResource(id = R.string.icon_play_pause),
                tint = if (isBottomTab) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(AppTheme.dimens.grid_3)
            )
        }
    }
}

/**
 * A composable function that displays a "Next" icon.
 *
 * @param onClick The action to be performed when the icon is clicked.
 * @param isBottomTab A boolean indicating whether the icon is part of the bottom tab.
 */
@Composable
fun NextIcon(onClick: () -> Unit, isBottomTab: Boolean) {
    IconButton(onClick = onClick, modifier = Modifier.padding(AppTheme.dimens.grid_1)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_next),
            contentDescription = stringResource(id = R.string.icon_skip_next),
            tint = if (isBottomTab) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(AppTheme.dimens.grid_3)
        )
    }
}

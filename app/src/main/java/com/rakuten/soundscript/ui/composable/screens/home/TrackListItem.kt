package com.rakuten.soundscript.ui.composable.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.rakuten.soundscript.R
import com.rakuten.soundscript.data.models.Track
import com.rakuten.soundscript.player.PlayerStates.STATE_PLAYING
import com.rakuten.soundscript.ui.UIConstants
import com.rakuten.soundscript.ui.composable.TrackImage
import com.rakuten.soundscript.ui.composable.common.GradientOverlay
import com.rakuten.soundscript.ui.composable.common.LinearGradient
import com.rakuten.soundscript.ui.theme.AppTheme
import com.rakuten.soundscript.ui.theme.Typography

/**
 * A composable function that displays a list item for a track.
 * The list item includes the track's image, name, and artist.
 * Also includes a click action for the track.
 *
 * @param track The track to be displayed.
 * @param onTrackClick The action to be performed when the track item is clicked.
 */
@Composable
fun TrackListItem(
    width: Dp,
    track: Track,
    onTrackClick: () -> Unit,
    onTranscriptClick: (String) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    val itemHeight = width.times(1.33f)

    Box(
        modifier = Modifier
            .clickable {
                onTranscriptClick(track._id)
            }
    ) {
        Box {
            TrackImage(
                trackImageUrl = track.heroImage ?: "",
                modifier = Modifier
                    .width(width)
                    .height(itemHeight)
            )

            // Overlay
            GradientOverlay(
                modifier = Modifier
                    .width(width)
                    .height(itemHeight),
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

        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = track.name,
                fontWeight = Typography.bodyMedium.fontWeight,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )

            if(!track.artist.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(AppTheme.dimens.grid_0_5))
                Text(
                    text = track.artist,
                    fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = textColor.copy(alpha = 0.7f)
                )
            }

            Row(
                modifier = Modifier.padding(vertical = AppTheme.dimens.grid_1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(modifier = Modifier
                    .clip(RoundedCornerShape(UIConstants.CORNER_RADIUS_XXL))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(AppTheme.dimens.grid_0_5),
                    onClick = { onTrackClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "Play/Pause Icon",
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (track.state == STATE_PLAYING) {
                    LottieAudioWave(modifier = Modifier)
                }
            }
        }
    }
}

/**
 * A composable function that displays a Lottie animation of an audio wave.
 * The animation loops indefinitely.
 */
@Composable
fun LottieAudioWave(modifier: Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.audio_wave))
    LottieAnimation(
        composition = composition,
        iterations = Int.MAX_VALUE,
        modifier = modifier.size(size = 48.dp)
    )
}
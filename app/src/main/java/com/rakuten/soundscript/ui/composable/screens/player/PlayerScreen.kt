package com.rakuten.soundscript.ui.composable.screens.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rakuten.soundscript.R
import com.rakuten.soundscript.ui.Actions
import com.rakuten.soundscript.ui.composable.TrackControls
import com.rakuten.soundscript.ui.composable.TrackInfo
import com.rakuten.soundscript.ui.composable.TrackProgressSlider
import com.rakuten.soundscript.ui.theme.AppTheme
import com.rakuten.soundscript.utils.DialogParams


@Composable
fun PlayerScreen(params: DialogParams.PlayerScreenParams, onBackClick: () -> Unit) {

    val systemUiController = rememberSystemUiController()
    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(Color.Transparent)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TrackInfo(
            playbackState = params.playbackState,
            trackName = params.selectedTrack.name,
            artistName = params.selectedTrack.artist ?: "",
            heroImage = params.selectedTrack.heroImage ?: "",
            bannerImages = params.selectedTrack.images ?: emptyList(),
            onBackClick = onBackClick
        )
        
        Spacer(modifier = Modifier.height(AppTheme.dimens.grid_3))

        TrackProgressSlider(playbackState = params.playbackState, amplitudes = params.amplitudes) {
            params.playerEvents.onSeekBarPositionChanged(it)
        }

        TrackControls(
            selectedTrack = params.selectedTrack,
            onPreviousClick = params.playerEvents::onPreviousClick,
            onPlayPauseClick = params.playerEvents::onPlayPauseClick,
            onNextClick = params.playerEvents::onNextClick
        )
    }
}
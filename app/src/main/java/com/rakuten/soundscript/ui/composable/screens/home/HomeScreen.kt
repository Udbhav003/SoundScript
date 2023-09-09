@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)

package com.rakuten.soundscript.ui.composable.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import com.rakuten.soundscript.R
import com.rakuten.soundscript.data.models.Track
import com.rakuten.soundscript.player.PlaybackState
import com.rakuten.soundscript.player.PlayerEvents
import com.rakuten.soundscript.ui.Actions
import com.rakuten.soundscript.ui.composable.BottomSheetDialog
import com.rakuten.soundscript.ui.composable.common.GridLayout
import com.rakuten.soundscript.ui.composable.screens.player.BottomPlayerTab
import com.rakuten.soundscript.ui.theme.AppTheme
import com.rakuten.soundscript.utils.DialogParams
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * A composable function that hosts the home screen of the application.
 * It manages the state of the bottom sheet and when to show it.
 *
 * @param viewModel The ViewModel that is responsible for providing data to the UI and processing user actions.
 */
@Composable
fun HomeScreen(navigation: NavHostController, viewModel: HomeScreenViewModel) {
    val fullScreenState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()
    val onBottomTabClick: () -> Unit = { scope.launch { fullScreenState.show() } }

    TrackList(
        navigation = navigation,
        viewModel = viewModel,
        tracks = viewModel.tracks,
        selectedTrack = viewModel.selectedTrack,
        fullScreenState = fullScreenState,
        playerEvents = viewModel,
        playbackState = viewModel.playbackState,
        onBottomTabClick = onBottomTabClick,
        onNavigateToTranscriptSummary = {
            Actions(navigation).gotoTranscriptSummary(it)
        }
    )
}

/**
 * A composable function that displays a list of tracks and a bottom sheet dialog.
 * The bottom sheet dialog is shown when a track is selected.
 *
 * @param tracks A list of tracks to be displayed.
 * @param selectedTrack The currently selected track. If null, no track is selected.
 * @param fullScreenState The state of the bottom sheet dialog.
 * @param playerEvents The events that the player can trigger.
 * @param playbackState The state of the media playback.
 * @param onBottomTabClick A lambda function that is invoked when the bottom tab is clicked.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackList(
    navigation: NavHostController,
    viewModel: HomeScreenViewModel,
    tracks: List<Track>,
    selectedTrack: Track?,
    fullScreenState: ModalBottomSheetState,
    playerEvents: PlayerEvents,
    playbackState: StateFlow<PlaybackState>,
    onBottomTabClick: () -> Unit,
    onNavigateToTranscriptSummary: (String) -> Unit
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val hideBottomSheetDialog: () -> Unit = { coroutineScope.launch { fullScreenState.hide() } }
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        systemUiController.setStatusBarColor(Color.Transparent)
    }

    if (uiState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(AppTheme.dimens.grid_5), color = MaterialTheme.colorScheme.tertiary)
        }
    } else {
        ModalBottomSheetLayout(
            sheetContent = {
                if (selectedTrack != null && uiState.amplitudes.isNotEmpty()) {
                    Surface {
                        BottomSheetDialog(
                            navigation,
                            DialogParams.PlayerScreenParams(
                                selectedTrack,
                                playerEvents,
                                playbackState,
                                uiState.amplitudes
                            ),
                            hideBottomSheetDialog
                        )
                    }
                }
            },
            sheetState = fullScreenState,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            sheetElevation = 8.dp
        ) {
            Scaffold { contentPadding ->
                Box {
                    Column {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            item {
                                Row(
                                    modifier = Modifier.padding(AppTheme.dimens.grid_2)
                                        .statusBarsPadding(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.app_logo),
                                        contentDescription = "App Logo"
                                    )
                                    Spacer(modifier = Modifier.width(AppTheme.dimens.grid_1_5))
                                    Text(
                                        text = context.getString(R.string.app_name),
                                        fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                                    )
                                }
                                Spacer(modifier = Modifier.height(AppTheme.dimens.grid_2))
                            }

                            item {
                                GridLayout(
                                    items = tracks,
                                    columns = 2,
                                    contentPadding = PaddingValues(
                                        horizontal = AppTheme.dimens.grid_2
                                    ),
                                    itemSpacing = AppTheme.dimens.grid_2
                                ) { item, width ->
                                    TrackListItem(
                                        width = width,
                                        track = item,
                                        onTrackClick = {
                                            viewModel.fetchAmplitudesByTrackUrl(context, item.audio)
                                            playerEvents.onTrackClick(item)
                                        },
                                        onTranscriptClick = { id -> onNavigateToTranscriptSummary(id) }
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = selectedTrack != null,
                            enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight })
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                BottomPlayerTab(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .navigationBarsPadding()
                                        .padding(vertical = AppTheme.dimens.grid_1_5),
                                    selectedTrack!!,
                                    playbackState,
                                    playerEvents,
                                    onBottomTabClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
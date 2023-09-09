package com.rakuten.soundscript.ui.composable.screens.transcript

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.rakuten.soundscript.R
import com.rakuten.soundscript.ui.UIConstants
import com.rakuten.soundscript.ui.composable.common.ExpandingText
import com.rakuten.soundscript.ui.theme.AppTheme
import com.rakuten.soundscript.utils.DialogParams
import com.rakuten.soundscript.utils.noRippleClickable

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TranscriptScreen(
    params: DialogParams.TranscriptScreenParams,
    navigation: NavHostController,
    viewModel: TranscriptScreenViewModel
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var isTranscriptExpanded by remember { mutableStateOf(false) }
    var isTranscriptSummaryExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchContentDetail(params.contentId)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            item {
                Text(
                    modifier = Modifier.padding(
                        AppTheme.dimens.grid_2
                    ),
                    text = context.getString(R.string.app_name),
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )

                if (!uiState.trackDetail?.heroImage.isNullOrEmpty()) {
                    GlideImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.dimens.grid_2),
                        model = uiState.trackDetail?.heroImage,
                        contentDescription = "Hero Image"
                    )
                }

                Text(
                    modifier = Modifier.padding(
                        start = AppTheme.dimens.grid_2,
                        end = AppTheme.dimens.grid_2,
                        top = AppTheme.dimens.grid_2
                    ),
                    text = context.getString(R.string.heading_transcript),
                    fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.grid_2)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.dimens.grid_1)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            modifier = Modifier.noRippleClickable {
                                isTranscriptExpanded = !isTranscriptExpanded
                            },
                            painter = painterResource(id = if (isTranscriptExpanded) R.drawable.ic_shrink else R.drawable.ic_expand),
                            contentDescription = "Expand Icon"
                        )
                    }

                    ExpandingText(
                        modifier = Modifier
                            .border(
                                AppTheme.dimens.grid_0_25,
                                MaterialTheme.colorScheme.onBackground,
                                RoundedCornerShape(UIConstants.CORNER_RADIUS_REG)
                            )
                            .padding(AppTheme.dimens.grid_2),
                        text = uiState.trackDetail?.transcription ?: "",
                        isExpanded = isTranscriptExpanded,
                        setExpanded = {
                            isTranscriptExpanded = it
                        }
                    )
                }

                Text(
                    modifier = Modifier.padding(
                        start = AppTheme.dimens.grid_2,
                        end = AppTheme.dimens.grid_2,
                        top = AppTheme.dimens.grid_2
                    ),
                    text = context.getString(R.string.heading_transcript_summary),
                    fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.grid_2)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.dimens.grid_1)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            modifier = Modifier.noRippleClickable {
                                isTranscriptSummaryExpanded = !isTranscriptSummaryExpanded
                            },
                            painter = painterResource(id = if (isTranscriptSummaryExpanded) R.drawable.ic_shrink else R.drawable.ic_expand),
                            contentDescription = "Expand Icon"
                        )
                    }

                    ExpandingText(
                        modifier = Modifier
                            .border(
                                AppTheme.dimens.grid_0_25,
                                MaterialTheme.colorScheme.onBackground,
                                RoundedCornerShape(UIConstants.CORNER_RADIUS_REG)
                            )
                            .padding(AppTheme.dimens.grid_2),
                        text = uiState.trackDetail?.summary ?: "",
                        isExpanded = isTranscriptSummaryExpanded,
                        setExpanded = {
                            isTranscriptSummaryExpanded = it
                        }
                    )
                }
                Spacer(modifier = Modifier.height(AppTheme.dimens.grid_5))
            }
        }
    }
}
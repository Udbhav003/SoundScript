package com.rakuten.soundscript.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rakuten.soundscript.ui.Destinations
import com.rakuten.soundscript.ui.composable.screens.home.HomeScreen
import com.rakuten.soundscript.ui.composable.screens.transcript.TranscriptScreen
import com.rakuten.soundscript.ui.theme.MusicPlayerJetpackComposeTheme
import com.rakuten.soundscript.utils.DialogParams
import com.rakuten.soundscript.ui.composable.screens.home.HomeScreenViewModel
import com.rakuten.soundscript.ui.composable.screens.transcript.TranscriptScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * [MainActivity] is the main entry point of the app.
 * It is annotated with [@AndroidEntryPoint] to enable field injection via Hilt.
 * This class extends [ComponentActivity], which is a lean version of [AppCompatActivity].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * The [HomeScreenViewModel] instance is obtained via the [viewModels] delegate,
     * which uses the activity as the ViewModelStoreOwner.
     */
    private val homeViewModel: HomeScreenViewModel by viewModels()
    private val transcriptScreenViewModel: TranscriptScreenViewModel by viewModels()

    /**
     * The [onCreate] method is called when the activity is starting.
     * It sets up the UI content and associates it with the activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     * then this Bundle contains the data it most recently supplied in [onSaveInstanceState].
     * Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Set the theme of the app to MusicPlayerJetpackComposeTheme.
            MusicPlayerJetpackComposeTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Destinations.Home) {

                    composable(
                        route = Destinations.Home,
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        }) {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            HomeScreen(navigation = navController, homeViewModel)
                        }
                    }

                    composable(route = "${Destinations.TranscriptSummary}/{${Destinations.TranscriptSummaryArgs.contentId}}",
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        },
                        arguments = listOf(
                            navArgument(Destinations.TranscriptSummaryArgs.contentId) {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->

                        val contentId =
                            backStackEntry.arguments?.getString(Destinations.TranscriptSummaryArgs.contentId)

                        if (!contentId.isNullOrEmpty()) {
                            Surface(modifier = Modifier.fillMaxSize()) {
                                TranscriptScreen(
                                    params = DialogParams.TranscriptScreenParams(contentId),
                                    navigation = navController,
                                    viewModel = transcriptScreenViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
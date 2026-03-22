package dev.krgm4d.shiroguessr.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.krgm4d.shiroguessr.R
import androidx.navigation.toRoute
import dev.krgm4d.shiroguessr.navigation.Screen
import dev.krgm4d.shiroguessr.service.InterstitialAdManager
import dev.krgm4d.shiroguessr.service.ShareService
import dev.krgm4d.shiroguessr.service.TutorialManager
import dev.krgm4d.shiroguessr.ui.component.GameHeader
import dev.krgm4d.shiroguessr.ui.component.TutorialBottomSheet
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.viewmodel.GameMode
import dev.krgm4d.shiroguessr.viewmodel.ResultViewModel
import kotlinx.coroutines.launch

/**
 * Root screen of the application.
 *
 * Uses a [NavHost] to switch between the Start screen (mode selection),
 * Classic, Map, and Result screens. The default start destination is
 * [Screen.Start], which shows the redesigned mode selection cards.
 *
 * The [GameHeader] is shown on all screens except the Start screen,
 * where mode selection is handled by the cards instead.
 *
 * Corresponds to the iOS `RootView`.
 */
@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    tutorialManager: TutorialManager? = null,
) {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val resultViewModel: ResultViewModel = viewModel()

    val context = LocalContext.current
    val manager = remember(context) { tutorialManager ?: TutorialManager(context) }
    var showTutorial by remember { mutableStateOf(!manager.hasShownTutorial) }

    val shareService = remember { ShareService() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Determine if we are on the Start screen to hide the GameHeader
    val isOnStartScreen = currentRoute
        ?.contains(Screen.Start::class.qualifiedName.orEmpty()) == true

    if (showTutorial) {
        TutorialBottomSheet(
            onDismiss = {
                manager.markTutorialAsShown()
                showTutorial = false
            },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
    ) { scaffoldPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
        ) {
            // Hide the header on the Start screen; show it during gameplay
            if (!isOnStartScreen) {
                GameHeader(
                    onModeButtonTap = {
                        // Navigate back to the Start screen for mode selection
                        navController.navigate(Screen.Start) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            NavHost(
                navController = navController,
                startDestination = Screen.Start,
                modifier = Modifier.weight(1f),
            ) {
                composable<Screen.Start> {
                    StartScreen(
                        onClassicSelected = {
                            navController.navigate(Screen.Classic(autoStart = true)) {
                                popUpTo(navController.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onMapSelected = {
                            navController.navigate(Screen.Map(autoStart = true)) {
                                popUpTo(navController.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                    )
                }
                composable<Screen.Classic> { backStackEntry ->
                    val route = backStackEntry.toRoute<Screen.Classic>()
                    ClassicGameScreen(
                        autoStart = route.autoStart,
                        onGameCompleted = { gameState ->
                            resultViewModel.setGameState(gameState, GameMode.Classic)
                            navController.navigate(Screen.Result(gameMode = GameMode.Classic.name)) {
                                popUpTo(navController.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                    )
                }
                composable<Screen.Map> { backStackEntry ->
                    val route = backStackEntry.toRoute<Screen.Map>()
                    MapGameScreen(
                        autoStart = route.autoStart,
                        onGameCompleted = { gameState ->
                            resultViewModel.setGameState(gameState, GameMode.Map)
                            navController.navigate(Screen.Result(gameMode = GameMode.Map.name)) {
                                popUpTo(navController.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                    )
                }
                composable<Screen.Result> { backStackEntry ->
                    val resultRoute = backStackEntry.toRoute<Screen.Result>()
                    val gameMode = GameMode.valueOf(resultRoute.gameMode)

                    // Preload the next interstitial ad when entering the result screen
                    LaunchedEffect(Unit) {
                        InterstitialAdManager.loadAd(context)
                    }

                    val navigateToNextGame = {
                        resultViewModel.clearGameState()
                        val target: Screen = when (gameMode) {
                            GameMode.Classic -> Screen.Classic(autoStart = true)
                            GameMode.Map -> Screen.Map(autoStart = true)
                        }
                        // Pop the entire back stack (including start destination)
                        // and navigate to the target game mode, so "Play Again"
                        // always returns to the same mode the user was playing.
                        navController.navigate(target) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }

                    ResultScreen(
                        onPlayAgain = {
                            val activity = context as? Activity
                            if (activity != null) {
                                // Show interstitial ad, then navigate on dismiss
                                InterstitialAdManager.showAd(activity) {
                                    navigateToNextGame()
                                }
                            } else {
                                // Fallback: navigate without ad if Activity unavailable
                                navigateToNextGame()
                            }
                        },
                        onShareResults = {
                            val gameState = resultViewModel.gameState.value
                            if (gameState != null) {
                                shareService.shareResults(context, gameState)
                            }
                        },
                        onCopyToClipboard = {
                            val gameState = resultViewModel.gameState.value
                            if (gameState != null) {
                                shareService.copyToClipboard(context, gameState)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        context.getString(R.string.share_copied_to_clipboard),
                                    )
                                }
                            }
                        },
                        resultViewModel = resultViewModel,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RootScreenPreview() {
    ShiroGuessrAndroidTheme {
        RootScreen()
    }
}

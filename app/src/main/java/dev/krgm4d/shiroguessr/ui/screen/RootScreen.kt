package dev.krgm4d.shiroguessr.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.krgm4d.shiroguessr.navigation.Screen
import dev.krgm4d.shiroguessr.service.TutorialManager
import dev.krgm4d.shiroguessr.ui.component.GameHeader
import dev.krgm4d.shiroguessr.ui.component.TutorialBottomSheet
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.viewmodel.GameMode
import dev.krgm4d.shiroguessr.viewmodel.ResultViewModel

/**
 * Root screen of the application.
 *
 * Displays the [GameHeader] at the top and uses a [NavHost] to switch
 * the content area between Classic, Map, and Result screens. The default
 * start destination is [Screen.Map], matching the iOS version's behaviour.
 *
 * Corresponds to the iOS `RootView`.
 */
@Composable
fun RootScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val resultViewModel: ResultViewModel = viewModel()

    val context = LocalContext.current
    val tutorialManager = remember { TutorialManager(context) }
    var showTutorial by remember { mutableStateOf(!tutorialManager.hasShownTutorial) }

    if (showTutorial) {
        TutorialBottomSheet(
            onDismiss = {
                tutorialManager.markTutorialAsShown()
                showTutorial = false
            },
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        GameHeader(
            onModeButtonTap = {
                val isOnClassic = currentRoute
                    ?.contains(Screen.Classic::class.qualifiedName.orEmpty()) == true
                val target: Screen = if (isOnClassic) Screen.Map else Screen.Classic

                navController.navigate(target) {
                    // Pop up to the start destination so that pressing back
                    // does not cycle through previously visited modes.
                    popUpTo(Screen.Map) { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        NavHost(
            navController = navController,
            startDestination = Screen.Map,
            modifier = Modifier.weight(1f),
        ) {
            composable<Screen.Classic> {
                ClassicGameScreen(
                    onGameCompleted = { gameState ->
                        resultViewModel.setGameState(gameState, GameMode.Classic)
                        navController.navigate(Screen.Result) {
                            popUpTo(Screen.Classic) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable<Screen.Map> {
                MapGameScreen(
                    onGameCompleted = { gameState ->
                        resultViewModel.setGameState(gameState, GameMode.Map)
                        navController.navigate(Screen.Result) {
                            popUpTo(Screen.Map) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable<Screen.Result> {
                ResultScreen(
                    onPlayAgain = {
                        val mode = resultViewModel.gameMode.value
                        resultViewModel.clearGameState()
                        val target: Screen = when (mode) {
                            GameMode.Classic -> Screen.Classic
                            GameMode.Map -> Screen.Map
                        }
                        navController.navigate(target) {
                            popUpTo(Screen.Map) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    resultViewModel = resultViewModel,
                )
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

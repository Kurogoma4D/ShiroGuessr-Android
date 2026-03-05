package dev.krgm4d.shiroguessr.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.krgm4d.shiroguessr.ui.component.GameHeader
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Game mode toggle, mirroring the iOS `GameMode` enum.
 */
enum class GameMode {
    Classic,
    Map,
}

/**
 * Root screen of the application.
 *
 * Displays the [GameHeader] at the top and switches the content area
 * between Classic and Map modes. The default mode is [GameMode.Map],
 * matching the iOS version's behaviour.
 *
 * Corresponds to the iOS `RootView`.
 */
@Composable
fun RootScreen(modifier: Modifier = Modifier) {
    var currentMode by rememberSaveable { mutableStateOf(GameMode.Map) }

    Column(modifier = modifier.fillMaxSize()) {
        GameHeader(
            onModeButtonTap = {
                currentMode = when (currentMode) {
                    GameMode.Classic -> GameMode.Map
                    GameMode.Map -> GameMode.Classic
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        when (currentMode) {
            GameMode.Classic -> ClassicGameScreen(modifier = Modifier.weight(1f))
            GameMode.Map -> MapGameScreen(modifier = Modifier.weight(1f))
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

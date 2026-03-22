package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.ui.theme.AccentContainer
import dev.krgm4d.shiroguessr.ui.theme.AccentPrimary
import dev.krgm4d.shiroguessr.ui.theme.CanvasDeep
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Header component displaying the app title and a mode toggle button.
 *
 * Corresponds to the iOS version's `GameHeader.swift`.
 * Uses Shiro Gallery colors: CanvasDeep background, AccentPrimary/AccentContainer
 * for the mode toggle button.
 *
 * @param onModeButtonTap Callback invoked when the mode toggle button is tapped.
 * @param modifier Optional [Modifier] for the root layout.
 */
@Composable
fun GameHeader(
    onModeButtonTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = CanvasDeep,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onModeButtonTap,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = AccentContainer,
                    contentColor = AccentPrimary,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp),
                )
                Text(
                    text = stringResource(R.string.game_mode),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameHeaderPreview() {
    ShiroGuessrAndroidTheme {
        GameHeader(onModeButtonTap = {})
    }
}

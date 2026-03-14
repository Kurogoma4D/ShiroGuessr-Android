package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Game action button: shows "Guess" before submission and "Next Round" after.
 *
 * Corresponds to the iOS version's `GameControls.swift`.
 * Uses a single button that switches its label and action based on state,
 * preventing layout shifts during transitions.
 *
 * @param canSubmit Whether the submit button should be enabled
 * @param canProceed Whether the "Next Round" state should be shown
 * @param onSubmit Callback when the submit button is pressed
 * @param onNext Callback when the "Next Round" button is pressed
 * @param modifier Optional modifier for the root layout
 */
@Composable
fun GameControls(
    canSubmit: Boolean,
    canProceed: Boolean,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MdFilledButton(
        onClick = if (canProceed) onNext else onSubmit,
        enabled = canSubmit || canProceed,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        if (canProceed) {
            Text(text = stringResource(R.string.controls_next_round))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowCircleRight,
                contentDescription = null,
            )
        } else {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.controls_guess))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameControlsSubmitPreview() {
    ShiroGuessrAndroidTheme {
        GameControls(
            canSubmit = true,
            canProceed = false,
            onSubmit = {},
            onNext = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameControlsNextPreview() {
    ShiroGuessrAndroidTheme {
        GameControls(
            canSubmit = false,
            canProceed = true,
            onSubmit = {},
            onNext = {},
        )
    }
}

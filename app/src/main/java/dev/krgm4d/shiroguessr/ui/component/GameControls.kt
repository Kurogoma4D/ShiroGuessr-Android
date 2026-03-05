package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

/**
 * Game action buttons: "Guess" to submit an answer and "Next Round" to proceed.
 *
 * Corresponds to the iOS version's `GameControls.swift`.
 * Shows the submit button when waiting for an answer, and switches
 * to a "Next Round" button after submission, with a spring animation
 * for the transition.
 *
 * @param canSubmit Whether the submit button should be enabled
 * @param canProceed Whether the "Next Round" button should be shown
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
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        // Submit button (shown before answer is submitted)
        AnimatedVisibility(
            visible = !canProceed,
            enter = fadeIn(animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f)) +
                slideInVertically(animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f)),
            exit = fadeOut() + slideOutVertically(),
        ) {
            MdFilledButton(
                onClick = onSubmit,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Guess")
            }
        }

        // Next round button (shown after answer is submitted)
        AnimatedVisibility(
            visible = canProceed,
            enter = fadeIn(animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f)) +
                slideInVertically(animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f)),
            exit = fadeOut() + slideOutVertically(),
        ) {
            MdFilledButton(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Next Round")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowCircleRight,
                    contentDescription = null,
                )
            }
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

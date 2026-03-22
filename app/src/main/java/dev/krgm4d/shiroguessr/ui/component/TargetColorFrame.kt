package dev.krgm4d.shiroguessr.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.RGBColor
import dev.krgm4d.shiroguessr.ui.theme.JetBrainsMonoFontFamily
import dev.krgm4d.shiroguessr.ui.theme.SampleBorder
import dev.krgm4d.shiroguessr.ui.theme.SampleFrameNeutral
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrTheme

/**
 * Gallery-frame style display for the target color in Classic mode.
 *
 * Renders the target color as a full-width banner framed by thin border lines
 * above and below, evoking a gallery exhibit aesthetic. The color sample sits
 * within a neutral-gray mid-value frame to prevent near-white colors from
 * blending into the dark background.
 *
 * The CSS value is hidden during gameplay (it could serve as a hint) and only
 * shown on the result screen via [showCSSValue].
 *
 * @param targetColor The RGB color to display
 * @param showCSSValue Whether to display the CSS color string below the frame
 * @param modifier Optional modifier for the root layout
 */
@Composable
fun TargetColorFrame(
    targetColor: RGBColor,
    showCSSValue: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        // "TARGET" label -- small, muted text to let the color command attention
        Text(
            text = stringResource(R.string.game_target_label),
            style = MaterialTheme.typography.labelSmall,
            color = ShiroGuessrTheme.colors.textMuted,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        // Top border line (gallery frame)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(SampleBorder),
        )

        // Neutral-gray frame containing the color sample
        // The neutral mid-value prevents white colors from blending into the background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SampleFrameNeutral)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            // Color sample banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(targetColor.toComposeColor()),
            )
        }

        // Bottom border line (gallery frame)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(SampleBorder),
        )

        // CSS value -- only shown on result screen to avoid giving hints during gameplay
        if (showCSSValue) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = targetColor.toCSSString(),
                style = MaterialTheme.typography.bodySmall,
                color = ShiroGuessrTheme.colors.textMuted,
                fontFamily = JetBrainsMonoFontFamily,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
@Composable
private fun TargetColorFramePreview() {
    ShiroGuessrAndroidTheme {
        TargetColorFrame(
            targetColor = RGBColor(r = 250, g = 248, b = 252),
            showCSSValue = false,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D12)
@Composable
private fun TargetColorFrameWithCSSPreview() {
    ShiroGuessrAndroidTheme {
        TargetColorFrame(
            targetColor = RGBColor(r = 250, g = 248, b = 252),
            showCSSValue = true,
            modifier = Modifier.padding(16.dp),
        )
    }
}

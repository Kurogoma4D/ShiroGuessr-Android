package dev.krgm4d.shiroguessr.service

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import dev.krgm4d.shiroguessr.R
import dev.krgm4d.shiroguessr.model.GameState
import java.text.NumberFormat

/**
 * Service for generating share text and performing share/clipboard operations.
 *
 * The share text format matches the iOS version's `ShareService.swift` exactly,
 * including star rating calculation and score formatting.
 */
class ShareService {

    companion object {
        private const val SHARE_URL = "https://shiro-guessr.pages.dev/app"
        private const val HASHTAG = "#白Guessr"
        private const val HEADER = "白Guessr \uD83C\uDFA8"
        private const val MAX_SCORE = "5,000"
        private const val STAR = "\u2B50"
    }

    /**
     * Generates the star rating string for a round based on distance.
     *
     * Star rating thresholds match the iOS version's `generateStarRating`:
     * - Distance 0-5: 5 stars
     * - Distance 6-10: 4 stars
     * - Distance 11-20: 3 stars
     * - Distance 21-40: 2 stars
     * - Distance 41+ or null: 1 star
     *
     * @param distance The Manhattan distance for the round, or null if not available
     * @return A string of star emoji characters
     */
    fun generateStarRating(distance: Int?): String {
        val starCount = when {
            distance == null -> 1
            distance <= 5 -> 5
            distance <= 10 -> 4
            distance <= 20 -> 3
            distance <= 40 -> 2
            else -> 1
        }
        return STAR.repeat(starCount)
    }

    /**
     * Formats a score with comma-separated thousands (e.g. 4100 -> "4,100").
     *
     * Uses [NumberFormat.getNumberInstance] for locale-independent formatting
     * that always produces comma-separated output matching the iOS version.
     *
     * @param score The numeric score to format
     * @return The formatted score string
     */
    fun formatScore(score: Int): String {
        return NumberFormat.getNumberInstance(java.util.Locale.US).format(score)
    }

    /**
     * Generates the full share text for a completed game.
     *
     * The format exactly matches the iOS version's `generateShareText`:
     * 1. Header line: "白Guessr" with palette emoji
     * 2. Score line with localized label
     * 3. Blank line
     * 4. Round-by-round results with star ratings and distances
     * 5. Blank line
     * 6. Share URL
     * 7. Blank line
     * 8. Hashtag
     *
     * @param context Android context for accessing localized string resources
     * @param gameState The completed game state
     * @return The formatted share text, or empty string if game is not completed
     */
    fun generateShareText(context: Context, gameState: GameState): String {
        if (!gameState.isCompleted) return ""

        return generateShareText(
            gameState = gameState,
            scoreLabel = context.getString(R.string.share_score),
            roundLabelFormat = context.getString(R.string.share_round),
            distanceLabel = context.getString(R.string.share_distance),
        )
    }

    /**
     * Generates the full share text for a completed game using provided labels.
     *
     * This overload accepts localized string labels directly, enabling unit
     * testing without an Android [Context].
     *
     * @param gameState The completed game state
     * @param scoreLabel Localized label for score (e.g. "Score:" or "スコア:")
     * @param roundLabelFormat Localized format for round (e.g. "Round %d:" or "ラウンド%d:")
     * @param distanceLabel Localized label for distance (e.g. "Distance:" or "距離:")
     * @return The formatted share text, or empty string if game is not completed
     */
    internal fun generateShareText(
        gameState: GameState,
        scoreLabel: String,
        roundLabelFormat: String,
        distanceLabel: String,
    ): String {
        if (!gameState.isCompleted) return ""

        val sb = StringBuilder()
        sb.append(HEADER).append("\n")
        sb.append(scoreLabel)
            .append(" ")
            .append(formatScore(gameState.totalScore))
            .append(" / ")
            .append(MAX_SCORE)
            .append("\n\n")

        for (round in gameState.rounds) {
            val stars = generateStarRating(round.distance)
            val distance = round.distance ?: 0
            sb.append(String.format(roundLabelFormat, round.roundNumber))
                .append(" ")
                .append(stars)
                .append(" (")
                .append(distanceLabel)
                .append(" ")
                .append(distance)
                .append(")\n")
        }

        sb.append("\n")
        sb.append(SHARE_URL)
        sb.append("\n\n")
        sb.append(HASHTAG)

        return sb.toString()
    }

    /**
     * Opens the Android share sheet with the game results text.
     *
     * Uses [Intent.ACTION_SEND] to launch the system share dialog,
     * allowing the user to share results via any installed app.
     *
     * @param context Android context for starting the share activity
     * @param gameState The completed game state to share
     */
    fun shareResults(context: Context, gameState: GameState) {
        val text = generateShareText(context, gameState)
        if (text.isEmpty()) return

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    /**
     * Copies the game results text to the system clipboard.
     *
     * Uses [ClipboardManager] to set the clipboard content with the
     * formatted share text.
     *
     * @param context Android context for accessing the clipboard service
     * @param gameState The completed game state to copy
     */
    fun copyToClipboard(context: Context, gameState: GameState) {
        val text = generateShareText(context, gameState)
        if (text.isEmpty()) return

        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ShiroGuessr Result", text)
        clipboardManager.setPrimaryClip(clip)
    }
}

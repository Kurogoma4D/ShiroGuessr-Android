package dev.krgm4d.shiroguessr.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Shiro Gallery Dark Color System for ShiroGuessr.
 *
 * Dark-base color palette designed to maximize visibility of white color samples.
 * A dark background makes subtle differences between whites much easier to perceive.
 */

// Surface & Background
val CanvasDeep = Color(0xFF0D0D12)       // メイン背景
val CanvasElevated = Color(0xFF1A1A22)   // カード/パネル背景
val CanvasSubtle = Color(0xFF252530)     // セカンダリ面

// Accent: Warm Gold
val AccentPrimary = Color(0xFFC9A96E)    // ゴールドアクセント — CTA、選択、スコア
val AccentSecondary = Color(0xFF8B7A5E)  // ミュートゴールド
val AccentContainer = Color(0xFF2A2520)  // アクセント背景

// Text
val TextPrimary = Color(0xFFE8E6E3)      // メインテキスト
val TextSecondary = Color(0xFF9995A0)    // セカンダリテキスト
val TextMuted = Color(0xFF5C5866)        // 控えめテキスト

// Feedback
val ScoreHigh = Color(0xFF7EC88B)        // 高スコア
val ScoreMid = Color(0xFFC9A96E)         // 中スコア
val ScoreLow = Color(0xFFC87E7E)         // 低スコア
val TimerWarning = Color(0xFFD4956B)     // タイマー警告
val TimerCritical = Color(0xFFC87E7E)    // タイマー危険

// Color Sample Display
val SampleBorder = Color(0xFF3A3A45)     // 色サンプル枠線
val SampleFrameNeutral = Color(0xFF787880) // ニュートラルグレーフレーム（白色の溶け込み防止）

// Error Container (dark variant)
val ErrorContainerDark = Color(0xFF3D1F1F)  // エラー背景

/**
 * Custom color palette for ShiroGuessr, provided via CompositionLocal.
 *
 * These colors extend beyond Material 3's built-in color roles to support
 * game-specific UI needs such as score feedback and sample display.
 */
@Immutable
data class ShiroGuessrColors(
    val canvasDeep: Color = CanvasDeep,
    val canvasElevated: Color = CanvasElevated,
    val canvasSubtle: Color = CanvasSubtle,
    val accentPrimary: Color = AccentPrimary,
    val accentSecondary: Color = AccentSecondary,
    val accentContainer: Color = AccentContainer,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textMuted: Color = TextMuted,
    val scoreHigh: Color = ScoreHigh,
    val scoreMid: Color = ScoreMid,
    val scoreLow: Color = ScoreLow,
    val timerWarning: Color = TimerWarning,
    val timerCritical: Color = TimerCritical,
    val sampleBorder: Color = SampleBorder,
    val sampleFrameNeutral: Color = SampleFrameNeutral,
)

val LocalShiroGuessrColors = staticCompositionLocalOf { ShiroGuessrColors() }

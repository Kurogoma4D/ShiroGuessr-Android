package dev.krgm4d.shiroguessr.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dev.krgm4d.shiroguessr.R

/**
 * Google Fonts provider used to load fonts at runtime.
 */
private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

// ---------------------------------------------------------------------------
// Font Definitions
// ---------------------------------------------------------------------------

// NOTE: DmSerifDisplayFontFamily, OutfitFontFamily, and NotoSansJpFontFamily are downloaded
// at runtime via Google Mobile Services (GMS) font provider. If GMS is unavailable (e.g. on
// devices without Google Play Services), these fonts will fall back to system defaults.
// Bundling all three font families would add significant APK size, so runtime download is preferred.

/**
 * DM Serif Display -- used for Display role (scores, large numbers).
 * Serif elegance for the gallery aesthetic.
 */
private val dmSerifDisplayGoogleFont = GoogleFont("DM Serif Display")

val DmSerifDisplayFontFamily = FontFamily(
    Font(
        googleFont = dmSerifDisplayGoogleFont,
        fontProvider = googleFontProvider,
        weight = FontWeight.Normal,
    ),
)

/**
 * Outfit -- used for Headline and Label roles.
 * Geometric sans-serif that pairs well with the gold accent.
 */
private val outfitGoogleFont = GoogleFont("Outfit")

val OutfitFontFamily = FontFamily(
    Font(
        googleFont = outfitGoogleFont,
        fontProvider = googleFontProvider,
        weight = FontWeight.Normal,
    ),
    Font(
        googleFont = outfitGoogleFont,
        fontProvider = googleFontProvider,
        weight = FontWeight.Medium,
    ),
    Font(
        googleFont = outfitGoogleFont,
        fontProvider = googleFontProvider,
        weight = FontWeight.SemiBold,
    ),
    Font(
        googleFont = outfitGoogleFont,
        fontProvider = googleFontProvider,
        weight = FontWeight.Bold,
    ),
)

/**
 * Noto Sans JP -- used for Body role.
 * Unified Japanese + Latin glyph coverage.
 */
private val notoSansJpGoogleFont = GoogleFont("Noto Sans JP")

val NotoSansJpFontFamily = FontFamily(
    Font(
        googleFont = notoSansJpGoogleFont,
        fontProvider = googleFontProvider,
        weight = FontWeight.Normal,
    ),
    Font(
        googleFont = notoSansJpGoogleFont,
        fontProvider = googleFontProvider,
        weight = FontWeight.Medium,
    ),
)

/**
 * JetBrains Mono -- bundled monospace font for CSS values and distance display.
 */
val JetBrainsMonoFontFamily = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
)

// ---------------------------------------------------------------------------
// Material Design 3 Typography Scale for ShiroGuessr ("Shiro Gallery")
//
// Font pairing follows the design guideline:
//   Display  -> DM Serif Display (serif elegance, gold-colored in usage)
//   Headline -> Outfit Bold/SemiBold (letter-spacing +0.02em)
//   Title    -> Outfit SemiBold/Medium
//   Body     -> Noto Sans JP Regular
//   Label    -> Outfit Medium (letter-spacing +0.02em, no ALL CAPS)
// ---------------------------------------------------------------------------

val Typography = Typography(
    // Display -- DM Serif Display
    displayLarge = TextStyle(
        fontFamily = DmSerifDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = DmSerifDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = DmSerifDisplayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),

    // Headline -- Outfit Bold/SemiBold with +0.02em letter-spacing
    headlineLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.02.em,
    ),
    headlineMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.02.em,
    ),
    headlineSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.02.em,
    ),

    // Title -- Outfit SemiBold/Medium
    titleLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.02.em,
    ),
    titleMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.02.em,
    ),
    titleSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.02.em,
    ),

    // Body -- Noto Sans JP
    bodyLarge = TextStyle(
        fontFamily = NotoSansJpFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansJpFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSansJpFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),

    // Label -- Outfit Medium with +0.02em letter-spacing
    labelLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.02.em,
    ),
    labelMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.02.em,
    ),
    labelSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.02.em,
    ),
)

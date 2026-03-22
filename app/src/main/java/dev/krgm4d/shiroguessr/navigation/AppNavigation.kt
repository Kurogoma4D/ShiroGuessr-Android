package dev.krgm4d.shiroguessr.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations for the app.
 *
 * Uses Kotlin Serialization with Navigation Compose for compile-time
 * safe route definitions.
 */
sealed interface Screen {
    /** Start screen with mode selection cards. */
    @Serializable
    data object Start : Screen

    /** Classic color-guessing mode. */
    @Serializable
    data class Classic(val autoStart: Boolean = false) : Screen

    /** Map-based color-guessing mode. */
    @Serializable
    data class Map(val autoStart: Boolean = false) : Screen

    /** Results screen shown after a game round. */
    @Serializable
    data class Result(val gameMode: String) : Screen
}

package dev.krgm4d.shiroguessr.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations for the app.
 *
 * Uses Kotlin Serialization with Navigation Compose for compile-time
 * safe route definitions.
 */
sealed interface Screen {
    /** Classic color-guessing mode. */
    @Serializable
    data object Classic : Screen

    /** Map-based color-guessing mode. */
    @Serializable
    data object Map : Screen

    /** Results screen shown after a game round. */
    @Serializable
    data object Result : Screen
}

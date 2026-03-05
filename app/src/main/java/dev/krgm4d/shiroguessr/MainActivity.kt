package dev.krgm4d.shiroguessr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds
import dev.krgm4d.shiroguessr.service.InterstitialAdManager
import dev.krgm4d.shiroguessr.ui.screen.RootScreen
import dev.krgm4d.shiroguessr.ui.theme.ShiroGuessrAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(this)

        // Preload the first interstitial ad
        InterstitialAdManager.loadAd(this)

        setContent {
            ShiroGuessrAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RootScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

package com.petlingo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import com.petlingo.app.data.SettingsStore
import com.petlingo.app.ui.PetLingoApp
import com.petlingo.app.ui.theme.PetLingoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val settingsStore = SettingsStore(this)
        setContent {
            val settings by settingsStore.settings.collectAsState()
            PetLingoTheme(themeMode = settings.themeMode) {
                PetLingoApp(settingsStore = settingsStore)
            }
        }
    }
}

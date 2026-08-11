package com.nekonihon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import com.nekonihon.app.data.SettingsStore
import com.nekonihon.app.ui.NekoNihonApp
import com.nekonihon.app.ui.theme.NekoNihonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val settingsStore = SettingsStore(this)
        setContent {
            val settings by settingsStore.settings.collectAsState()
            NekoNihonTheme(themeMode = settings.themeMode) {
                NekoNihonApp(settingsStore = settingsStore)
            }
        }
    }
}

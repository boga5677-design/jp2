package com.petlingo.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AppSettings(
    val speechRate: Float = 1.0f,
    val defaultQuestionCount: Int = 20,
    val soundEffects: Boolean = true,
    val autoReadQuestion: Boolean = false,
    val showExplanation: Boolean = true,
    val addWrongAnswerAutomatically: Boolean = true,
    val dailyGoal: Int = 20,
    val themeMode: String = "淺色",
    val largeText: Boolean = false,
    val dailyReminder: Boolean = false
)

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("petlingo_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(load())
    val settings: StateFlow<AppSettings> = _settings

    fun update(value: AppSettings) {
        _settings.value = value
        prefs.edit()
            .putFloat("speechRate", value.speechRate)
            .putInt("defaultQuestionCount", value.defaultQuestionCount)
            .putBoolean("soundEffects", value.soundEffects)
            .putBoolean("autoReadQuestion", value.autoReadQuestion)
            .putBoolean("showExplanation", value.showExplanation)
            .putBoolean("addWrongAnswerAutomatically", value.addWrongAnswerAutomatically)
            .putInt("dailyGoal", value.dailyGoal)
            .putString("themeMode", value.themeMode)
            .putBoolean("largeText", value.largeText)
            .putBoolean("dailyReminder", value.dailyReminder)
            .apply()
    }

    fun reset() {
        prefs.edit().clear().apply()
        _settings.value = AppSettings()
    }

    private fun load() = AppSettings(
        speechRate = prefs.getFloat("speechRate", 1.0f),
        defaultQuestionCount = prefs.getInt("defaultQuestionCount", 20),
        soundEffects = prefs.getBoolean("soundEffects", true),
        autoReadQuestion = prefs.getBoolean("autoReadQuestion", false),
        showExplanation = prefs.getBoolean("showExplanation", true),
        addWrongAnswerAutomatically = prefs.getBoolean("addWrongAnswerAutomatically", true),
        dailyGoal = prefs.getInt("dailyGoal", 20),
        themeMode = prefs.getString("themeMode", "淺色") ?: "淺色",
        largeText = prefs.getBoolean("largeText", false),
        dailyReminder = prefs.getBoolean("dailyReminder", false)
    )
}

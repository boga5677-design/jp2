package com.petlingo.app.data

import android.content.Context

class FavoriteStore(context: Context) {
    private val prefs = context.getSharedPreferences("petlingo_favorites", Context.MODE_PRIVATE)

    fun load(): Set<Int> = prefs.getStringSet("ids", emptySet()).orEmpty().mapNotNull { it.toIntOrNull() }.toSet()

    fun save(ids: Set<Int>) {
        prefs.edit().putStringSet("ids", ids.map(Int::toString).toSet()).apply()
    }
}

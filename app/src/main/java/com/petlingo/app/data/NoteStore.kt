package com.petlingo.app.data

import android.content.Context
import com.petlingo.app.model.StudyNote
import org.json.JSONArray
import org.json.JSONObject

class NoteStore(context: Context) {
    private val prefs = context.getSharedPreferences("petlingo_notes", Context.MODE_PRIVATE)

    fun load(): List<StudyNote> {
        val raw = prefs.getString("items", "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        StudyNote(
                            key = obj.optString("key"),
                            category = obj.optString("category"),
                            kind = obj.optString("kind"),
                            title = obj.optString("title"),
                            content = obj.optString("content"),
                            detail = obj.optString("detail"),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }
        }.getOrElse { emptyList() }
    }

    fun toggle(note: StudyNote): List<StudyNote> {
        val current = load().toMutableList()
        val index = current.indexOfFirst { it.key == note.key }
        if (index >= 0) {
            current.removeAt(index)
        } else {
            current.add(0, note)
        }
        save(current)
        return current
    }

    fun remove(key: String): List<StudyNote> {
        val updated = load().filterNot { it.key == key }
        save(updated)
        return updated
    }

    fun clear(): List<StudyNote> {
        prefs.edit().remove("items").apply()
        return emptyList()
    }

    private fun save(items: List<StudyNote>) {
        val array = JSONArray()
        items.forEach { note ->
            array.put(
                JSONObject()
                    .put("key", note.key)
                    .put("category", note.category)
                    .put("kind", note.kind)
                    .put("title", note.title)
                    .put("content", note.content)
                    .put("detail", note.detail)
                    .put("createdAt", note.createdAt)
            )
        }
        prefs.edit().putString("items", array.toString()).apply()
    }
}

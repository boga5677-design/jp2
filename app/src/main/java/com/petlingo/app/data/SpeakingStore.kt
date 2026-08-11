package com.petlingo.app.data

import android.content.Context
import com.petlingo.app.model.SpeakingRecord

class SpeakingStore(context: Context) {
    private val prefs = context.getSharedPreferences("speaking_history", Context.MODE_PRIVATE)

    fun load(): List<SpeakingRecord> {
        val raw = prefs.getString("records", "").orEmpty()
        if (raw.isBlank()) return emptyList()
        return raw.lineSequence().mapNotNull { line ->
            val parts = line.split('\t')
            if (parts.size < 6) null else runCatching {
                SpeakingRecord(
                    id = parts[0].toLong(),
                    targetText = decode(parts[1]),
                    recognizedText = decode(parts[2]),
                    score = parts[3].toInt(),
                    accent = decode(parts[4]),
                    createdAt = parts[5].toLong()
                )
            }.getOrNull()
        }.sortedByDescending { it.createdAt }.toList()
    }

    fun add(record: SpeakingRecord) {
        val updated = (listOf(record) + load()).take(100)
        prefs.edit().putString("records", updated.joinToString("\n") { encodeRecord(it) }).apply()
    }

    fun clear() = prefs.edit().remove("records").apply()

    private fun encodeRecord(r: SpeakingRecord): String = listOf(
        r.id.toString(), encode(r.targetText), encode(r.recognizedText), r.score.toString(),
        encode(r.accent), r.createdAt.toString()
    ).joinToString("\t")

    private fun encode(value: String): String = value
        .replace("%", "%25").replace("\t", "%09").replace("\n", "%0A")

    private fun decode(value: String): String = value
        .replace("%0A", "\n").replace("%09", "\t").replace("%25", "%")
}

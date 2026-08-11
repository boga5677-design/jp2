package com.petlingo.app.util

import kotlin.math.max

object SpeakingScorer {
    fun score(target: String, recognized: String): Int {
        val a = normalize(target)
        val b = normalize(recognized)
        if (a.isBlank() || b.isBlank()) return 0
        val distance = levenshtein(a, b)
        val longest = max(a.length, b.length).coerceAtLeast(1)
        return ((1.0 - distance.toDouble() / longest) * 100).toInt().coerceIn(0, 100)
    }

    fun feedback(target: String, recognized: String, score: Int): String = when {
        recognized.isBlank() -> "沒有取得辨識結果，請靠近麥克風後再試一次。"
        score >= 95 -> "非常接近目標內容，發音清楚自然。"
        score >= 85 -> "整體很好，可再注意尾音與重音。"
        score >= 70 -> "內容大致正確，建議放慢速度並清楚念出每個音節。"
        else -> "辨識差異較大，先播放示範音，再分段跟讀。"
    }

    private fun normalize(text: String): String = text.lowercase()
        .replace(Regex("[^a-z0-9' ]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()

    private fun levenshtein(a: String, b: String): Int {
        var previous = IntArray(b.length + 1) { it }
        for (i in a.indices) {
            val current = IntArray(b.length + 1)
            current[0] = i + 1
            for (j in b.indices) {
                val cost = if (a[i] == b[j]) 0 else 1
                current[j + 1] = minOf(
                    current[j] + 1,
                    previous[j + 1] + 1,
                    previous[j] + cost
                )
            }
            previous = current
        }
        return previous[b.length]
    }
}

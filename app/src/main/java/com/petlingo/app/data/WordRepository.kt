package com.petlingo.app.data

import android.content.Context
import com.petlingo.app.model.Word

class WordRepository(private val context: Context) {
    fun loadWords(limit: Int = 10000): List<Word> = runCatching {
        context.assets.open("japanese_words.tsv").bufferedReader().useLines { lines ->
            lines.drop(1).mapNotNull { line ->
                val p = line.split('	')
                if (p.size < 7) return@mapNotNull null
                Word(
                    id = p[0].toIntOrNull() ?: return@mapNotNull null,
                    english = p[1].trim(),
                    chinese = p[2].trim(),
                    partOfSpeech = p[3].trim(),
                    note = p[4].trim(),
                    level = p[5].trim(),
                    academic = p[6].trim(),
                    ceecLevel = p.getOrNull(7)?.trim().orEmpty()
                )
            }.filter { it.english.isNotBlank() && it.chinese.isNotBlank() }
                .take(limit)
                .toList()
        }
    }.getOrElse {
        listOf(
            Word(1, "ありがとう", "謝謝", "感動詞", note = "ありがとう / arigatou", level = "N5", academic = "問候", ceecLevel = "1"),
            Word(2, "すみません", "不好意思；請問", "感動詞", note = "すみません / sumimasen", level = "N5", academic = "問候", ceecLevel = "1"),
            Word(3, "学生", "學生", "名詞", note = "がくせい / gakusei", level = "N5", academic = "人物", ceecLevel = "2"),
            Word(4, "駅", "車站", "名詞", note = "えき / eki", level = "N5", academic = "交通", ceecLevel = "4")
        )
    }
}

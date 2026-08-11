package com.petlingo.app.data

import android.content.Context
import com.petlingo.app.model.ReadingAnswerRecord
import com.petlingo.app.model.ReadingSession
import org.json.JSONArray
import org.json.JSONObject

class ReadingStore(context: Context) {
    private val prefs = context.getSharedPreferences("petlingo_reading", Context.MODE_PRIVATE)

    fun save(session: ReadingSession) {
        val all = JSONArray(prefs.getString("sessions", "[]"))
        val answers = JSONArray()
        session.answers.forEach { a ->
            answers.put(JSONObject()
                .put("index", a.questionIndex)
                .put("prompt", a.prompt)
                .put("selected", a.selectedAnswer)
                .put("correct", a.correctAnswer)
                .put("ok", a.isCorrect)
                .put("elapsed", a.elapsedMillis)
                .put("explanation", a.explanation))
        }
        all.put(JSONObject()
            .put("id", session.id)
            .put("passageId", session.passageId)
            .put("title", session.passageTitle)
            .put("category", session.category)
            .put("startedAt", session.startedAt)
            .put("finishedAt", session.finishedAt)
            .put("answers", answers))
        prefs.edit().putString("sessions", all.toString()).apply()
    }

    fun load(): List<ReadingSession> = runCatching {
        val all = JSONArray(prefs.getString("sessions", "[]"))
        (0 until all.length()).map { i ->
            val o = all.getJSONObject(i)
            val arr = o.getJSONArray("answers")
            val answers = (0 until arr.length()).map { j ->
                val a = arr.getJSONObject(j)
                ReadingAnswerRecord(
                    questionIndex = a.getInt("index"),
                    prompt = a.getString("prompt"),
                    selectedAnswer = a.getString("selected"),
                    correctAnswer = a.getString("correct"),
                    isCorrect = a.getBoolean("ok"),
                    elapsedMillis = a.getLong("elapsed"),
                    explanation = a.optString("explanation", "")
                )
            }
            ReadingSession(
                id = o.getLong("id"),
                passageId = o.getInt("passageId"),
                passageTitle = o.getString("title"),
                category = o.getString("category"),
                startedAt = o.getLong("startedAt"),
                finishedAt = o.getLong("finishedAt"),
                answers = answers
            )
        }.reversed()
    }.getOrDefault(emptyList())

    fun clear() = prefs.edit().clear().apply()
}

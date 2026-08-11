package com.petlingo.app.data

import android.content.Context
import com.petlingo.app.model.AnswerRecord
import com.petlingo.app.model.QuestionType
import com.petlingo.app.model.QuizSession
import org.json.JSONArray
import org.json.JSONObject

class StudyStore(context: Context) {
    private val prefs = context.getSharedPreferences("petlingo_study", Context.MODE_PRIVATE)

    fun saveSession(session: QuizSession) {
        val all = JSONArray(prefs.getString("sessions", "[]"))
        val obj = JSONObject()
            .put("id", session.id)
            .put("startedAt", session.startedAt)
            .put("finishedAt", session.finishedAt)
            .put("modeLabel", session.modeLabel)
        val answers = JSONArray()
        session.answers.forEach { a ->
            answers.put(
                JSONObject()
                    .put("questionId", a.questionId)
                    .put("prompt", a.prompt)
                    .put("selected", a.selectedAnswer)
                    .put("correct", a.correctAnswer)
                    .put("ok", a.isCorrect)
                    .put("elapsed", a.elapsedMillis)
                    .put("type", a.type.name)
                    .put("changed", a.changedAnswer)
                    .put("explanation", a.explanation)
                    .put("timestamp", a.timestamp)
            )
        }
        obj.put("answers", answers)
        all.put(obj)
        prefs.edit().putString("sessions", all.toString()).apply()
    }

    fun loadSessions(): List<QuizSession> = runCatching {
        val all = JSONArray(prefs.getString("sessions", "[]"))
        (0 until all.length()).map { i ->
            val o = all.getJSONObject(i)
            val arr = o.getJSONArray("answers")
            val answers = (0 until arr.length()).map { j ->
                val a = arr.getJSONObject(j)
                AnswerRecord(
                    questionId = a.getInt("questionId"),
                    prompt = a.getString("prompt"),
                    selectedAnswer = a.getString("selected"),
                    correctAnswer = a.getString("correct"),
                    isCorrect = a.getBoolean("ok"),
                    elapsedMillis = a.getLong("elapsed"),
                    type = QuestionType.valueOf(a.getString("type")),
                    changedAnswer = a.optBoolean("changed"),
                    explanation = a.optString("explanation", ""),
                    timestamp = a.optLong("timestamp")
                )
            }
            QuizSession(
                id = o.getLong("id"),
                startedAt = o.getLong("startedAt"),
                finishedAt = o.getLong("finishedAt"),
                answers = answers,
                modeLabel = o.optString("modeLabel", "日文選中文")
            )
        }.reversed()
    }.getOrDefault(emptyList())

    fun clear() = prefs.edit().clear().apply()
}

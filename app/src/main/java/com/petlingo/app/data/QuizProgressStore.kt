package com.petlingo.app.data

import android.content.Context
import com.petlingo.app.model.AnswerRecord
import com.petlingo.app.model.QuestionType
import com.petlingo.app.model.QuizMode
import com.petlingo.app.model.QuizQuestion
import org.json.JSONArray
import org.json.JSONObject

data class SavedQuizProgress(
    val questions: List<QuizQuestion>,
    val currentIndex: Int,
    val answers: List<AnswerRecord>,
    val mode: QuizMode,
    val quizStartedAt: Long,
    val questionStartedAt: Long,
    val currentQuestionSubmitted: Boolean
)

class QuizProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("petlingo_quiz_progress", Context.MODE_PRIVATE)

    fun save(progress: SavedQuizProgress) {
        val root = JSONObject()
            .put("currentIndex", progress.currentIndex)
            .put("mode", progress.mode.name)
            .put("quizStartedAt", progress.quizStartedAt)
            .put("questionStartedAt", progress.questionStartedAt)
            .put("currentQuestionSubmitted", progress.currentQuestionSubmitted)

        val questions = JSONArray()
        progress.questions.forEach { q ->
            questions.put(
                JSONObject()
                    .put("id", q.id)
                    .put("prompt", q.prompt)
                    .put("correctIndex", q.correctIndex)
                    .put("explanation", q.explanation)
                    .put("type", q.type.name)
                    .put("options", JSONArray(q.options))
            )
        }
        root.put("questions", questions)

        val answers = JSONArray()
        progress.answers.forEach { a ->
            answers.put(
                JSONObject()
                    .put("questionId", a.questionId)
                    .put("prompt", a.prompt)
                    .put("selectedAnswer", a.selectedAnswer)
                    .put("correctAnswer", a.correctAnswer)
                    .put("isCorrect", a.isCorrect)
                    .put("elapsedMillis", a.elapsedMillis)
                    .put("type", a.type.name)
                    .put("changedAnswer", a.changedAnswer)
                    .put("explanation", a.explanation)
                    .put("timestamp", a.timestamp)
            )
        }
        root.put("answers", answers)

        prefs.edit().putString("progress", root.toString()).apply()
    }

    fun load(): SavedQuizProgress? {
        val raw = prefs.getString("progress", null) ?: return null
        return runCatching {
            val root = JSONObject(raw)
            val questionsJson = root.getJSONArray("questions")
            val questions = buildList {
                for (i in 0 until questionsJson.length()) {
                    val q = questionsJson.getJSONObject(i)
                    val opts = q.getJSONArray("options")
                    add(
                        QuizQuestion(
                            id = q.getInt("id"),
                            prompt = q.getString("prompt"),
                            options = List(opts.length()) { opts.getString(it) },
                            correctIndex = q.getInt("correctIndex"),
                            explanation = q.optString("explanation"),
                            type = QuestionType.valueOf(q.getString("type"))
                        )
                    )
                }
            }

            val answersJson = root.optJSONArray("answers") ?: JSONArray()
            val answers = buildList {
                for (i in 0 until answersJson.length()) {
                    val a = answersJson.getJSONObject(i)
                    add(
                        AnswerRecord(
                            questionId = a.getInt("questionId"),
                            prompt = a.getString("prompt"),
                            selectedAnswer = a.getString("selectedAnswer"),
                            correctAnswer = a.getString("correctAnswer"),
                            isCorrect = a.getBoolean("isCorrect"),
                            elapsedMillis = a.getLong("elapsedMillis"),
                            type = QuestionType.valueOf(a.getString("type")),
                            changedAnswer = a.optBoolean("changedAnswer"),
                            explanation = a.optString("explanation"),
                            timestamp = a.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            if (questions.isEmpty()) return@runCatching null

            SavedQuizProgress(
                questions = questions,
                currentIndex = root.optInt("currentIndex").coerceIn(0, questions.lastIndex),
                answers = answers,
                mode = QuizMode.valueOf(root.optString("mode", QuizMode.ENGLISH_TO_CHINESE.name)),
                quizStartedAt = root.optLong("quizStartedAt", System.currentTimeMillis()),
                questionStartedAt = root.optLong("questionStartedAt", System.currentTimeMillis()),
                currentQuestionSubmitted = root.optBoolean("currentQuestionSubmitted", false)
            )
        }.getOrNull()
    }

    fun clear() {
        prefs.edit().remove("progress").apply()
    }
}

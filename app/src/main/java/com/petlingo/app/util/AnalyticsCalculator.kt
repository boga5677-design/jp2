package com.petlingo.app.util

import com.petlingo.app.model.AbilityMetrics
import com.petlingo.app.model.AnswerRecord
import com.petlingo.app.model.QuestionType
import com.petlingo.app.model.QuizSession
import kotlin.math.roundToInt

object AnalyticsCalculator {
    fun metrics(session: QuizSession?): AbilityMetrics {
        if (session == null || session.answers.isEmpty()) return AbilityMetrics(0f, 0f, 0f, 0f, 0f, 0f)
        fun typeScore(type: QuestionType): Float {
            val answers = session.answers.filter { it.type == type }
            return if (answers.isEmpty()) 0f else answers.count { it.isCorrect }.toFloat() / answers.size * 5f
        }
        val avgSec = averageMillis(session.answers) / 1000f
        val speed = (5f - (avgSec - 5f) / 5f).coerceIn(0f, 5f)
        val accuracy = session.correctCount.toFloat() / session.questionCount * 5f
        return AbilityMetrics(
            speed = speed,
            accuracy = accuracy,
            vocabulary = typeScore(QuestionType.VOCABULARY),
            grammar = typeScore(QuestionType.GRAMMAR),
            reading = typeScore(QuestionType.READING),
            listening = typeScore(QuestionType.LISTENING)
        )
    }

    fun averageMillis(answers: List<AnswerRecord>): Long =
        if (answers.isEmpty()) 0L else answers.sumOf { it.elapsedMillis } / answers.size

    fun averageCorrectMillis(session: QuizSession): Long = averageMillis(session.answers.filter { it.isCorrect })
    fun averageWrongMillis(session: QuizSession): Long = averageMillis(session.answers.filterNot { it.isCorrect })
    fun fastest(session: QuizSession): AnswerRecord? = session.answers.minByOrNull { it.elapsedMillis }
    fun slowest(session: QuizSession): AnswerRecord? = session.answers.maxByOrNull { it.elapsedMillis }
    fun changedCount(session: QuizSession): Int = session.answers.count { it.changedAnswer }

    fun insight(session: QuizSession): String {
        if (session.answers.isEmpty()) return "尚無足夠資料。"
        val correctAverage = averageCorrectMillis(session)
        val wrongAverage = averageWrongMillis(session)
        return when {
            session.score >= 90 && averageMillis(session.answers) <= 10_000L -> "正確率與速度都很穩定，可以提高題目難度。"
            wrongAverage > correctAverage * 1.4 && wrongAverage > 0L -> "答錯題通常花費較久，建議先複習相關單字，再進行限時練習。"
            changedCount(session) >= session.questionCount / 3 -> "本次修改答案的題目較多，作答時可先排除明顯錯誤選項再確認。"
            session.score < 60 -> "本次錯題較多，建議從錯題本重新練習，不需要追求作答速度。"
            else -> "整體表現穩定，可針對最慢與答錯題目進行複習。"
        }
    }

    fun seconds(ms: Long): String = "%.1f 秒".format(ms / 1000.0)

    fun totalTime(ms: Long): String {
        val totalSeconds = ms / 1000
        return "${totalSeconds / 60} 分 ${totalSeconds % 60} 秒"
    }

    fun level(value: Float): Float = (value * 2).roundToInt() / 2f
}

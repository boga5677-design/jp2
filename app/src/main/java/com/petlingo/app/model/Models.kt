package com.petlingo.app.model

data class Word(
    val id: Int,
    val english: String,
    val chinese: String,
    val partOfSpeech: String = "",
    val note: String = "",
    val level: String = "",
    val academic: String = "",
    val ceecLevel: String = ""
)

data class Phrase(
    val id: Int,
    val english: String,
    val chinese: String,
    val example: String
)


enum class QuestionType(val label: String) {
    VOCABULARY("單字"), PHRASE("片語"), GRAMMAR("文法"), CLOZE("克漏字"), READING("閱讀"), LISTENING("聽力")
}

enum class QuizMode(val label: String) {
    ENGLISH_TO_CHINESE("日文選中文"),
    CHINESE_TO_ENGLISH("中文選日文"),
    FAVORITES("收藏單字測驗"),
    PHRASE("片語測驗"),
    CLOZE("克漏字"),
    READING("閱讀測驗"),
    LISTENING("聽力測驗"),
    TOEIC_MOCK("綜合測驗")
}

data class QuizQuestion(
    val id: Int,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val type: QuestionType
)

data class AnswerRecord(
    val questionId: Int,
    val prompt: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val elapsedMillis: Long,
    val type: QuestionType,
    val changedAnswer: Boolean,
    val explanation: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class QuizSession(
    val id: Long = System.currentTimeMillis(),
    val startedAt: Long,
    val finishedAt: Long,
    val answers: List<AnswerRecord>,
    val modeLabel: String = "日文選中文"
) {
    val correctCount: Int get() = answers.count { it.isCorrect }
    val questionCount: Int get() = answers.size
    val score: Int get() = if (questionCount == 0) 0 else correctCount * 100 / questionCount
    val totalMillis: Long get() = answers.sumOf { it.elapsedMillis }
}

data class AbilityMetrics(
    val speed: Float,
    val accuracy: Float,
    val vocabulary: Float,
    val grammar: Float,
    val reading: Float,
    val listening: Float
)

data class ReadingQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class ReadingPassage(
    val id: Int,
    val title: String,
    val category: String,
    val content: String,
    val questions: List<ReadingQuestion>
)

data class ReadingAnswerRecord(
    val questionIndex: Int,
    val prompt: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val elapsedMillis: Long,
    val explanation: String
)

data class ReadingSession(
    val id: Long = System.currentTimeMillis(),
    val passageId: Int,
    val passageTitle: String,
    val category: String,
    val startedAt: Long,
    val finishedAt: Long,
    val answers: List<ReadingAnswerRecord>
) {
    val correctCount: Int get() = answers.count { it.isCorrect }
    val questionCount: Int get() = answers.size
    val score: Int get() = if (questionCount == 0) 0 else correctCount * 100 / questionCount
    val totalMillis: Long get() = answers.sumOf { it.elapsedMillis }
}

data class WrongAnswer(
    val key: String,
    val prompt: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val explanation: String,
    val typeLabel: String,
    val elapsedMillis: Long,
    val wrongCount: Int = 1,
    val lastWrongAt: Long = System.currentTimeMillis()
)


data class SpeakingRecord(
    val id: Long = System.currentTimeMillis(),
    val targetText: String,
    val recognizedText: String,
    val score: Int,
    val accent: String,
    val createdAt: Long = System.currentTimeMillis()
)


data class StudyNote(
    val key: String,
    val category: String,
    val kind: String,
    val title: String,
    val content: String,
    val detail: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

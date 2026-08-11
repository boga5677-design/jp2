package com.petlingo.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.petlingo.app.data.FavoriteStore
import com.petlingo.app.data.QuizRepository
import com.petlingo.app.data.QuizProgressStore
import com.petlingo.app.data.SavedQuizProgress
import com.petlingo.app.data.PhraseRepository
import com.petlingo.app.data.ReadingRepository
import com.petlingo.app.data.ReadingStore
import com.petlingo.app.data.StudyStore
import com.petlingo.app.data.SpeakingStore
import com.petlingo.app.data.SettingsStore
import com.petlingo.app.data.NoteStore
import com.petlingo.app.data.WordRepository
import com.petlingo.app.data.WrongAnswerStore
import com.petlingo.app.model.AnswerRecord
import com.petlingo.app.model.QuizMode
import com.petlingo.app.model.QuizQuestion
import com.petlingo.app.model.QuizSession
import com.petlingo.app.model.ReadingPassage
import com.petlingo.app.model.ReadingSession
import com.petlingo.app.model.Word
import com.petlingo.app.model.SpeakingRecord
import com.petlingo.app.model.WrongAnswer
import com.petlingo.app.model.StudyNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PetLingoViewModel(app: Application) : AndroidViewModel(app) {
    private val quizRepo = QuizRepository()
    private val store = StudyStore(app)
    private val readingStore = ReadingStore(app)
    private val favoriteStore = FavoriteStore(app)
    private val allWords = WordRepository(app).loadWords()
    private val wrongStore = WrongAnswerStore(app)
    private val speakingStore = SpeakingStore(app)
    private val settingsStore = SettingsStore(app)
    private val noteStore = NoteStore(app)
    private val progressStore = QuizProgressStore(app)
    val phrases = PhraseRepository().phrases()
    val readingPassages: List<ReadingPassage> = ReadingRepository().passages()

    private val _questions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val questions: StateFlow<List<QuizQuestion>> = _questions
    private val _sessions = MutableStateFlow(store.loadSessions())
    val sessions: StateFlow<List<QuizSession>> = _sessions
    private val _readingSessions = MutableStateFlow(readingStore.load())
    val readingSessions: StateFlow<List<ReadingSession>> = _readingSessions
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex
    private val _answers = MutableStateFlow<List<AnswerRecord>>(emptyList())
    val answers: StateFlow<List<AnswerRecord>> = _answers
    private val _currentCompletedSession = MutableStateFlow<QuizSession?>(null)
    val currentCompletedSession: StateFlow<QuizSession?> = _currentCompletedSession
    private val _analysisSession = MutableStateFlow<QuizSession?>(_sessions.value.firstOrNull())
    val analysisSession: StateFlow<QuizSession?> = _analysisSession
    private val _favorites = MutableStateFlow(favoriteStore.load())
    val favorites: StateFlow<Set<Int>> = _favorites
    private val _wrongAnswers = MutableStateFlow(wrongStore.load())
    val wrongAnswers: StateFlow<List<WrongAnswer>> = _wrongAnswers
    private val _speakingRecords = MutableStateFlow(speakingStore.load())
    val speakingRecords: StateFlow<List<SpeakingRecord>> = _speakingRecords
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query
    private val _notes = MutableStateFlow(noteStore.load())
    val notes: StateFlow<List<StudyNote>> = _notes


    private var currentMode = QuizMode.ENGLISH_TO_CHINESE
    private var quizStartedAt = 0L
    private var questionStartedAt = 0L
    private var firstChoice: Int? = null
    private var currentQuestionSubmitted = false

    private val _hasActiveQuiz = MutableStateFlow(false)
    val hasActiveQuiz: StateFlow<Boolean> = _hasActiveQuiz

    init {
        restoreQuizProgress()
    }

    private fun restoreQuizProgress() {
        val saved = progressStore.load() ?: return
        _questions.value = saved.questions
        _currentIndex.value = saved.currentIndex
        _answers.value = saved.answers
        currentMode = saved.mode
        quizStartedAt = saved.quizStartedAt
        questionStartedAt = System.currentTimeMillis()
        currentQuestionSubmitted = saved.currentQuestionSubmitted
        firstChoice = null
        _hasActiveQuiz.value = true
    }

    private fun saveQuizProgress() {
        if (_questions.value.isEmpty() || _currentCompletedSession.value != null) return
        progressStore.save(
            SavedQuizProgress(
                questions = _questions.value,
                currentIndex = _currentIndex.value,
                answers = _answers.value,
                mode = currentMode,
                quizStartedAt = quizStartedAt,
                questionStartedAt = questionStartedAt,
                currentQuestionSubmitted = currentQuestionSubmitted
            )
        )
        _hasActiveQuiz.value = true
    }

    fun resumeQuiz(): Boolean = _hasActiveQuiz.value && _questions.value.isNotEmpty()


    val words: List<Word> get() = allWords

    fun setQuery(value: String) { _query.value = value }

    fun filteredWords(): List<Word> {
        val q = _query.value.trim()
        return if (q.isBlank()) allWords else allWords.filter {
            it.english.contains(q, ignoreCase = true) || it.chinese.contains(q) || it.note.contains(q, ignoreCase = true) || it.academic.contains(q, ignoreCase = true)
        }
    }

    fun toggleFavorite(id: Int) {
        val updated = _favorites.value.toMutableSet().apply { if (!add(id)) remove(id) }
        _favorites.value = updated
        favoriteStore.save(updated)
    }

    fun newQuiz(count: Int = 10, mode: QuizMode = QuizMode.ENGLISH_TO_CHINESE): Boolean {
        val pool = if (mode == QuizMode.FAVORITES) allWords.filter { it.id in _favorites.value } else allWords
        if (pool.size < 4) return false
        currentMode = mode
        _questions.value = quizRepo.createQuiz(pool, phrases, count, mode)
        _currentIndex.value = 0
        _answers.value = emptyList()
        _currentCompletedSession.value = null
        quizStartedAt = System.currentTimeMillis()
        questionStartedAt = quizStartedAt
        firstChoice = null
        currentQuestionSubmitted = false
        progressStore.clear()
        saveQuizProgress()
        return true
    }

    fun select(index: Int) {
        if (!currentQuestionSubmitted && firstChoice == null) firstChoice = index
    }

    fun submit(index: Int): AnswerRecord? {
        if (currentQuestionSubmitted) return _answers.value.lastOrNull()
        val q = _questions.value.getOrNull(_currentIndex.value) ?: return null
        if (index !in q.options.indices) return null
        val elapsed = (System.currentTimeMillis() - questionStartedAt).coerceAtLeast(0L)
        val record = AnswerRecord(
            questionId = q.id,
            prompt = q.prompt,
            selectedAnswer = q.options[index],
            correctAnswer = q.options[q.correctIndex],
            isCorrect = index == q.correctIndex,
            elapsedMillis = elapsed,
            type = q.type,
            changedAnswer = firstChoice != null && firstChoice != index,
            explanation = q.explanation
        )
        currentQuestionSubmitted = true
        _answers.value = _answers.value + record
        saveQuizProgress()
        if (!record.isCorrect && settingsStore.settings.value.addWrongAnswerAutomatically) {
            addWrongAnswer(
                record.prompt,
                record.selectedAnswer,
                record.correctAnswer,
                q.explanation,
                record.type.label,
                record.elapsedMillis,
                "quiz-${record.questionId}"
            )
        }
        return record
    }

    fun next(): Boolean {
        return if (_currentIndex.value < _questions.value.lastIndex) {
            _currentIndex.value++
            questionStartedAt = System.currentTimeMillis()
            firstChoice = null
            currentQuestionSubmitted = false
            saveQuizProgress()
            true
        } else {
            finish()
            false
        }
    }

    private fun finish() {
        if (_answers.value.isEmpty()) return
        val session = QuizSession(
            startedAt = quizStartedAt,
            finishedAt = System.currentTimeMillis(),
            answers = _answers.value,
            modeLabel = currentMode.label
        )
        store.saveSession(session)
        _currentCompletedSession.value = session
        _analysisSession.value = session
        _sessions.value = store.loadSessions()
        progressStore.clear()
        _hasActiveQuiz.value = false
    }

    fun openAnalysis(session: QuizSession) { _analysisSession.value = session }

    fun saveReadingSession(session: ReadingSession) {
        readingStore.save(session)
        _readingSessions.value = readingStore.load()
    }

    fun addReadingWrong(prompt: String, selected: String, correct: String, explanation: String, elapsed: Long) {
        addWrongAnswer(prompt, selected, correct, explanation, "閱讀", elapsed, "reading-${prompt.hashCode()}")
    }

    private fun addWrongAnswer(
        prompt: String,
        selected: String,
        correct: String,
        explanation: String,
        type: String,
        elapsed: Long,
        key: String
    ) {
        wrongStore.add(WrongAnswer(key, prompt, selected, correct, explanation, type, elapsed))
        _wrongAnswers.value = wrongStore.load()
    }

    fun removeWrongAnswer(key: String) {
        wrongStore.remove(key)
        _wrongAnswers.value = wrongStore.load()
    }

    fun clearWrongAnswers() {
        wrongStore.clear()
        _wrongAnswers.value = emptyList()
    }

    fun readingPassage(id: Int): ReadingPassage? = readingPassages.firstOrNull { it.id == id }

    fun saveSpeakingRecord(record: SpeakingRecord) {
        speakingStore.add(record)
        _speakingRecords.value = speakingStore.load()
    }

    fun clearSpeakingHistory() {
        speakingStore.clear()
        _speakingRecords.value = emptyList()
    }


fun toggleNote(note: StudyNote) {
    _notes.value = noteStore.toggle(note)
}

fun removeNote(key: String) {
    _notes.value = noteStore.remove(key)
}

fun clearNotes() {
    _notes.value = noteStore.clear()
}

fun isNoted(key: String): Boolean = _notes.value.any { it.key == key }

    fun clearHistory() {
        store.clear()
        readingStore.clear()
        _sessions.value = emptyList()
        _readingSessions.value = emptyList()
        _currentCompletedSession.value = null
        _analysisSession.value = null
    }
}

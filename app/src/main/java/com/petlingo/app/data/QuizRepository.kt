package com.petlingo.app.data

import com.petlingo.app.model.*

class QuizRepository {
    private val examBank = ExamQuestionRepository().gsatVocabulary()

    fun createQuiz(words: List<Word>, phrases: List<Phrase>, count: Int = 10, mode: QuizMode = QuizMode.ENGLISH_TO_CHINESE): List<QuizQuestion> =
        when (mode) {
            QuizMode.TOEIC_MOCK -> mixedMock(count, phrases)
            QuizMode.PHRASE -> phraseQuiz(phrases, count)
            QuizMode.CLOZE -> clozeQuiz(count)
            QuizMode.READING -> readingQuiz(count)
            else -> wordQuiz(words, count, mode)
        }

    private fun wordQuiz(words: List<Word>, count: Int, mode: QuizMode): List<QuizQuestion> {
        if (words.size < 4) return emptyList()
        return words.shuffled().take(count.coerceAtMost(words.size)).mapIndexed { index, word ->
            when (mode) {
                QuizMode.CHINESE_TO_ENGLISH -> make(index, word.chinese, word.english, words.filter { it.id != word.id }.shuffled().take(3).map { it.english }, "${word.english}：${word.chinese}", QuestionType.VOCABULARY)
                QuizMode.LISTENING -> make(index, word.english, word.chinese, words.filter { it.id != word.id }.shuffled().take(3).map { it.chinese }, "${word.english}：${word.chinese}", QuestionType.LISTENING)
                else -> make(index, word.english, word.chinese, words.filter { it.id != word.id }.shuffled().take(3).map { it.chinese }, "${word.english}：${word.chinese}", QuestionType.VOCABULARY)
            }
        }
    }

    private fun phraseQuiz(phrases: List<Phrase>, count: Int) =
        phrases.shuffled().take(count.coerceAtMost(phrases.size)).mapIndexed { i,p ->
            make(3000+i,p.english,p.chinese,phrases.filter { it.id!=p.id }.shuffled().take(3).map { it.chinese },"${p.english}：${p.chinese}\n${p.example}",QuestionType.PHRASE)
        }

    private fun clozeQuiz(count: Int): List<QuizQuestion> {
        val b=listOf(
            q(4001,"Please submit the completed form ______ Friday afternoon.","by",listOf("during","since","among"),"by 表示不晚於指定時間。"),
            q(4002,"The flight was canceled ______ severe weather.","due to",listOf("instead of","apart from","along with"),"due to 表示由於。"),
            q(4003,"All visitors are required ______ photo identification.","to present",listOf("presenting","present","presented"),"be required to 後接原形動詞。"),
            q(4004,"The manager asked whether the report ______ ready.","was",listOf("is","be","being"),"間接問句配合過去式。"),
            q(4005,"Customers can receive a refund ______ they show the receipt.","provided that",listOf("despite","unless","whereas"),"provided that 表示只要。"),
            q(4006,"The equipment should be inspected ______ a regular basis.","on",listOf("at","for","with"),"on a regular basis。"),
            q(4007,"Ms. Lin is responsible ______ organizing the conference.","for",listOf("to","with","at"),"be responsible for。"),
            q(4008,"The new branch will open as soon as construction ______.","is completed",listOf("will complete","completed","has completing"),"時間子句使用現在式被動。"),
            q(4009,"Employees were informed ______ the change by email.","of",listOf("to","with","from"),"inform someone of something。"),
            q(4010,"The company has experienced rapid growth ______ the past two years.","over",listOf("beside","until","among"),"over the past two years。"),
            q(4011,"Neither the director nor the assistants ______ available today.","are",listOf("is","be","was"),"動詞與較近主詞一致。"),
            q(4012,"The brochure provides information ______ local attractions.","about",listOf("between","during","toward"),"information about。"),
            q(4013,"We recommend ______ reservations in advance.","making",listOf("make","made","to made"),"recommend 後接動名詞。"),
            q(4014,"The package arrived later ______ expected.","than",listOf("then","as","from"),"比較級搭配 than。"),
            q(4015,"The proposal was approved ______ a unanimous vote.","by",listOf("from","into","upon"),"by 表示方式。"),
            q(4016,"Please do not hesitate ______ us.","to contact",listOf("contacting","contacted","contact"),"hesitate to。"),
            q(4017,"The workshop is intended ______ new employees.","for",listOf("by","at","of"),"be intended for。"),
            q(4018,"Sales increased, ______ operating costs remained stable.","while",listOf("because of","unless","therefore"),"while 表示對比。"),
            q(4019,"The warranty is valid only if the product ______ properly.","is used",listOf("uses","using","will use"),"使用被動語態。"),
            q(4020,"Applicants must have at least two years ______ experience.","of",listOf("for","to","with"),"two years of experience。")
        )
        return List(count.coerceAtMost(80)){b[it%b.size].copy(id=4100+it)}.shuffled()
    }

    private fun readingQuiz(count: Int): List<QuizQuestion> {
        val b=ReadingRepository().passages().flatMap { p -> p.questions.mapIndexed { i,q ->
            QuizQuestion(5000+p.id*10+i,"${p.title}\n\n${p.content}\n\n${q.prompt}",q.options,q.correctIndex,q.explanation,QuestionType.READING)
        }}
        return List(count.coerceAtMost(80)){b[it%b.size].copy(id=6000+it)}.shuffled()
    }

    private fun mixedMock(count:Int,phrases:List<Phrase>):List<QuizQuestion>{
        val pool=examBank+clozeQuiz(20)+readingQuiz(16)+phraseQuiz(phrases,20)
        return List(count.coerceIn(10,100)){pool[it%pool.size].copy(id=7000+it)}.shuffled()
    }
    private fun make(id:Int,prompt:String,correct:String,d:List<String>,e:String,t:QuestionType):QuizQuestion{
        val o=(d.distinct().take(3)+correct).shuffled()
        return QuizQuestion(id,prompt,o,o.indexOf(correct),e,t)
    }
    private fun q(id:Int,p:String,c:String,d:List<String>,e:String)=make(id,p,c,d,e,QuestionType.CLOZE)
}

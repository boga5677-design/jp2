package com.petlingo.app.data

object PetData {
    val words = listOf(
        Word("accomplish", "完成；達成", "We can accomplish the goal together."),
        Word("accurate", "準確的", "The report contains accurate information."),
        Word("appointment", "預約；任命", "I have a dental appointment tomorrow."),
        Word("available", "可取得的；有空的", "The manager is available this afternoon."),
        Word("benefit", "益處；使受益", "Regular practice will benefit your English."),
        Word("confirm", "確認", "Please confirm your reservation by email."),
        Word("customer", "顧客", "The customer requested a refund."),
        Word("deadline", "截止期限", "The deadline is next Friday."),
        Word("efficient", "有效率的", "This is a more efficient workflow."),
        Word("equipment", "設備", "Safety equipment must be inspected regularly."),
        Word("feedback", "回饋", "Thank you for your helpful feedback."),
        Word("improve", "改善", "Reading daily can improve your vocabulary."),
        Word("maintain", "維持；保養", "We maintain the system every month."),
        Word("purchase", "購買", "You can purchase tickets online."),
        Word("schedule", "行程；安排", "The meeting is on today's schedule."),
        Word("require", "需要；要求", "This task requires careful planning."),
        Word("respond", "回應", "Please respond to the message soon."),
        Word("solution", "解決方案", "The team found a practical solution."),
        Word("temporary", "暫時的", "The office is temporarily closed."),
        Word("verify", "核實", "We need to verify the information.")
    )

    val quiz = listOf(
        QuizQuestion("Which word means「確認」?", listOf("confirm", "benefit", "schedule", "respond"), "confirm", "confirm = 確認；核實某件事。"),
        QuizQuestion("The manager is _____ this afternoon.", listOf("accurate", "available", "temporary", "efficient"), "available", "available 表示「有空的、可取得的」。"),
        QuizQuestion("Which word means「截止期限」?", listOf("deadline", "customer", "feedback", "equipment"), "deadline", "deadline 是工作或任務的截止時間。"),
        QuizQuestion("Regular practice will _____ your English.", listOf("purchase", "maintain", "improve", "verify"), "improve", "improve 表示改善、提升。"),
        QuizQuestion("The team found a practical _____.", listOf("appointment", "solution", "schedule", "benefit"), "solution", "solution 表示解決方案。")
    )
}

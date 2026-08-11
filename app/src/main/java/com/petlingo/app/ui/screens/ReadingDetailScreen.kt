package com.petlingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.ReadingAnswerRecord
import com.petlingo.app.model.ReadingPassage
import com.petlingo.app.model.ReadingSession
import com.petlingo.app.util.AnalyticsCalculator

@Composable
fun ReadingDetailScreen(
    passage: ReadingPassage?,
    onWrong: (
        String,
        String,
        String,
        String,
        Long
    ) -> Unit,
    onComplete: (ReadingSession) -> Unit
) {
    if (passage == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("找不到文章")
        }

        return
    }

    var index by remember {
        mutableIntStateOf(0)
    }

    var selected by remember {
        mutableStateOf<Int?>(null)
    }

    var submitted by remember {
        mutableStateOf(false)
    }

    var questionStartedAt by remember {
        mutableLongStateOf(
            System.currentTimeMillis()
        )
    }

    val passageStartedAt = remember {
        System.currentTimeMillis()
    }

    val answers = remember {
        mutableStateListOf<
            ReadingAnswerRecord
        >()
    }

    var finishedSession by remember {
        mutableStateOf<ReadingSession?>(null)
    }

    val question =
        passage.questions[index]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = passage.category,
            color =
                MaterialTheme
                    .colorScheme
                    .primary
        )

        Text(
            text = passage.title,
            style =
                MaterialTheme
                    .typography
                    .headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Card {
            Text(
                text = passage.content,
                modifier = Modifier.padding(16.dp),
                style =
                    MaterialTheme
                        .typography
                        .bodyLarge
            )
        }

        val completed = finishedSession

        if (completed != null) {
            Text(
                text = "本篇完成",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "答對 ${completed.correctCount}" +
                    " / ${completed.questionCount} 題"
            )

            Text(
                text =
                    "本篇分數：" +
                    "${completed.score} 分"
            )

            Text(
                text =
                    "總作答時間：" +
                    AnalyticsCalculator.totalTime(
                        completed.totalMillis
                    )
            )

            Text(
                text =
                    "平均每題：" +
                    AnalyticsCalculator.seconds(
                        if (
                            completed.questionCount == 0
                        ) {
                            0L
                        } else {
                            completed.totalMillis /
                                completed.questionCount
                        }
                    )
            )

            Text(
                text =
                    "本篇結果獨立保存，" +
                    "不併入其他測驗平均分數。"
            )

            completed.answers.forEachIndexed {
                    answerIndex,
                    answer ->

                Card {
                    Column(
                        modifier =
                            Modifier.padding(12.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text =
                                "Q${answerIndex + 1} " +
                                if (
                                    answer.isCorrect
                                ) {
                                    "答對"
                                } else {
                                    "答錯"
                                },
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "作答時間：" +
                                AnalyticsCalculator
                                    .seconds(
                                        answer.elapsedMillis
                                    )
                        )

                        Text(
                            text =
                                "${answer.selectedAnswer}" +
                                " → " +
                                answer.correctAnswer
                        )
                    }
                }
            }
        } else {
            Text(
                text =
                    "第 ${index + 1}/" +
                    "${passage.questions.size} 題",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = question.prompt,
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            question.options.forEachIndexed {
                    optionIndex,
                    option ->

                OutlinedButton(
                    onClick = {
                        if (!submitted) {
                            selected = optionIndex
                        }
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled = !submitted
                ) {
                    Text(
                        text =
                            (
                                if (
                                    selected ==
                                    optionIndex
                                ) {
                                    "✓ "
                                } else {
                                    ""
                                }
                            ) + option
                    )
                }
            }

            if (!submitted) {
                Button(
                    onClick = {
                        val selectedIndex =
                            selected
                                ?: return@Button

                        val elapsed =
                            System
                                .currentTimeMillis() -
                            questionStartedAt

                        val record =
                            ReadingAnswerRecord(
                                questionIndex =
                                    index,
                                prompt =
                                    question.prompt,
                                selectedAnswer =
                                    question.options[
                                        selectedIndex
                                    ],
                                correctAnswer =
                                    question.options[
                                        question
                                            .correctIndex
                                    ],
                                isCorrect =
                                    selectedIndex ==
                                    question
                                        .correctIndex,
                                elapsedMillis =
                                    elapsed,
                                explanation =
                                    question
                                        .explanation
                            )

                        answers.add(record)
                        submitted = true

                        if (!record.isCorrect) {
                            onWrong(
                                record.prompt,
                                record.selectedAnswer,
                                record.correctAnswer,
                                record.explanation,
                                record.elapsedMillis
                            )
                        }
                    },
                    enabled = selected != null,
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("送出答案")
                }
            } else {
                val latest = answers.last()

                Text(
                    text =
                        if (latest.isCorrect) {
                            "答對了！"
                        } else {
                            "答錯了"
                        },
                    color =
                        if (latest.isCorrect) {
                            MaterialTheme
                                .colorScheme
                                .primary
                        } else {
                            MaterialTheme
                                .colorScheme
                                .error
                        },
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "作答時間：" +
                        AnalyticsCalculator.seconds(
                            latest.elapsedMillis
                        )
                )

                Text(
                    text =
                        "正確答案：" +
                        latest.correctAnswer
                )

                Text(
                    text =
                        question.explanation
                )

                Button(
                    onClick = {
                        if (
                            index <
                            passage.questions
                                .lastIndex
                        ) {
                            index++
                            selected = null
                            submitted = false
                            questionStartedAt =
                                System
                                    .currentTimeMillis()
                        } else {
                            val session =
                                ReadingSession(
                                    passageId =
                                        passage.id,
                                    passageTitle =
                                        passage.title,
                                    category =
                                        passage.category,
                                    startedAt =
                                        passageStartedAt,
                                    finishedAt =
                                        System
                                            .currentTimeMillis(),
                                    answers =
                                        answers.toList()
                                )

                            onComplete(session)
                            finishedSession =
                                session
                        }
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text(
                        text =
                            if (
                                index <
                                passage.questions
                                    .lastIndex
                            ) {
                                "下一題"
                            } else {
                                "查看本篇結果"
                            }
                    )
                }
            }
        }
    }
}

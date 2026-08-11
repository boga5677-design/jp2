package com.petlingo.app.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.AnswerRecord
import com.petlingo.app.model.QuestionType
import com.petlingo.app.model.QuizQuestion
import com.petlingo.app.model.StudyNote
import com.petlingo.app.util.AnalyticsCalculator
import java.util.Locale

@Composable
fun QuizScreen(
    question: QuizQuestion?,
    index: Int,
    total: Int,
    noteKeys: Set<String>,
    onToggleNote: (StudyNote) -> Unit,
    onSelect: (Int) -> Unit,
    onSubmit: (Int) -> AnswerRecord?,
    onNext: () -> Boolean,
    onFinished: () -> Unit
) {
    var selected by remember(index) { mutableStateOf<Int?>(null) }
    var result by remember(index) { mutableStateOf<AnswerRecord?>(null) }

    val context = LocalContext.current
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember {
        TextToSpeech(context) { status -> ttsReady = status == TextToSpeech.SUCCESS }
    }

    DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    fun speak() {
        if (!ttsReady || question == null) return
        tts.language = Locale.JAPAN
        tts.setSpeechRate(0.88f)
        tts.speak(
            question.prompt,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "petlingo-question-${question.id}"
        )
    }

    LaunchedEffect(question?.id, ttsReady) {
        if (question?.type == QuestionType.LISTENING && ttsReady) speak()
    }

    if (question == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val category = questionCategory(question.type)
    val questionKey = "quiz-question-${question.type.name}-${question.prompt.hashCode()}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("第 ${index + 1} / $total 題", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))

            if (containsEnglish(question.prompt)) {
                IconButton(
                    onClick = {
                        onToggleNote(
                            StudyNote(
                                key = questionKey,
                                category = category,
                                kind = "日文題目",
                                title = question.prompt,
                                content = "正確答案：${question.options.getOrNull(question.correctIndex).orEmpty()}",
                                detail = question.explanation
                            )
                        )
                    }
                ) {
                    Icon(
                        if (questionKey in noteKeys) Icons.Default.Star else Icons.Default.StarBorder,
                        if (questionKey in noteKeys) "取消日文筆記" else "加入日文筆記",
                        tint = if (questionKey in noteKeys) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Text("❤️ 20", color = Color(0xFFE64A3B), fontWeight = FontWeight.Bold)
        }

        LinearProgressIndicator(
            progress = { if (total == 0) 0f else (index + 1f) / total },
            modifier = Modifier.fillMaxWidth().height(5.dp)
        )

        Spacer(Modifier.height(6.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            if (question.type == QuestionType.LISTENING) {
                Icon(
                    Icons.Default.Headphones,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(44.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                FilledTonalButton(
                    onClick = ::speak,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .heightIn(min = 44.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, null)
                    Spacer(Modifier.width(6.dp))
                    Text("播放／重播")
                }
            } else {
                Text(
                    question.prompt,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = ::speak,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(36.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, "播放發音", tint = Color(0xFF1684E8))
                }
            }

            question.options.forEachIndexed { optionIndex, option ->
                val submitted = result != null
                val correct = optionIndex == question.correctIndex
                val chosen = selected == optionIndex
                val optionKey = "quiz-option-${question.type.name}-${question.prompt.hashCode()}-${option.hashCode()}"

                val background = when {
                    submitted && correct -> Color(0xFF6FA968)
                    submitted && chosen && !correct -> Color(0xFFF05045)
                    else -> Color.Transparent
                }
                val foreground = if (submitted && (correct || chosen)) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = background,
                    border = if (!submitted || (!correct && !chosen)) {
                        ButtonDefaults.outlinedButtonBorder
                    } else null
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !submitted) {
                                selected = optionIndex
                                onSelect(optionIndex)
                                result = onSubmit(optionIndex)
                            }
                            .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (submitted && (correct || chosen)) {
                            Icon(
                                if (correct) Icons.Default.CheckCircle else Icons.Default.Close,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                        }

                        Text(
                            option,
                            modifier = Modifier.weight(1f),
                            color = foreground,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (containsEnglish(option)) {
                            IconButton(
                                onClick = {
                                    onToggleNote(
                                        StudyNote(
                                            key = optionKey,
                                            category = category,
                                            kind = "日文選項",
                                            title = option,
                                            content = question.prompt,
                                            detail = if (correct) {
                                                "此日文為正確答案。${question.explanation}"
                                            } else {
                                                "此日文不是正確答案。${question.explanation}"
                                            }
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    if (optionKey in noteKeys) Icons.Default.Star else Icons.Default.StarBorder,
                                    if (optionKey in noteKeys) "取消日文筆記" else "加入日文筆記",
                                    tint = if (optionKey in noteKeys) {
                                        if (submitted && (correct || chosen)) Color.White
                                        else MaterialTheme.colorScheme.primary
                                    } else {
                                        if (submitted && (correct || chosen)) Color.White
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }

            result?.let { answer ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (answer.isCorrect) Color(0xFFE5F3DF) else Color(0xFFFFE3DF)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            if (answer.isCorrect) "答對了！" else "不正確",
                            fontWeight = FontWeight.Bold,
                            color = if (answer.isCorrect) Color(0xFF3D7D46) else Color(0xFFD84B40)
                        )
                        if (!answer.isCorrect) {
                            Text(
                                "正確答案：${answer.correctAnswer}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (question.explanation.isNotBlank()) {
                            Text(question.explanation, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            "作答時間：${AnalyticsCalculator.seconds(answer.elapsedMillis)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(2.dp))
        }

        if (result != null) {
            Spacer(Modifier.height(6.dp))
            Button(
                onClick = {
                    if (!onNext()) onFinished()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (index + 1 >= total) "完成並查看結果" else "下一題",
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, null)
            }
        }
    }
}

private fun questionCategory(type: QuestionType): String = when (type) {
    QuestionType.VOCABULARY -> "單字測驗"
    QuestionType.PHRASE -> "片語測驗"
    QuestionType.GRAMMAR -> "文法測驗"
    QuestionType.CLOZE -> "克漏字"
    QuestionType.READING -> "閱讀測驗"
    QuestionType.LISTENING -> "聽力測驗"
}

private fun containsEnglish(text: String): Boolean = text.any { it in 'A'..'Z' || it in 'a'..'z' }

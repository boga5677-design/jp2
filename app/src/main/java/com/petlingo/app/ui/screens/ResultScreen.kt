package com.petlingo.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.petlingo.app.R
import com.petlingo.app.model.QuizSession
import com.petlingo.app.ui.components.RadarChart
import com.petlingo.app.util.AnalyticsCalculator

@Composable
fun ResultScreen(
    session: QuizSession?,
    onAnalytics: () -> Unit,
    onHome: () -> Unit,
    onBackToQuizSetup: () -> Unit
) {
    var showQuestionTimes by remember { mutableStateOf(false) }
    BackHandler { onBackToQuizSetup() }

    val metrics = AnalyticsCalculator.metrics(session)

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    "本次測驗完成",
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
            }

            item {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.petlingo_hero),
                            contentDescription = "可愛寵物替你加油",
                            modifier = Modifier.fillMaxWidth().heightIn(max = 155.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            encouragement(session?.score ?: 0),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (session == null) {
                item { Text("尚無測驗資料") }
            } else {
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ScoreBlock("${session.correctCount}/${session.questionCount}", "答對題數")
                        ScoreBlock("${session.score} 分", "本次分數")
                    }
                }

                item {
                    Text("本次能力雷達圖", fontWeight = FontWeight.Bold)
                    RadarChart(
                        values = listOf(
                            metrics.vocabulary,
                            metrics.grammar,
                            metrics.reading,
                            metrics.listening,
                            metrics.speed
                        ),
                        labels = listOf("單字能力", "文法能力", "閱讀能力", "聽力能力", "作答速度")
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showQuestionTimes = !showQuestionTimes }
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("總秒數", fontWeight = FontWeight.Bold)
                                Text(
                                    String.format("%.1f 秒", session.totalMillis / 1000.0),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black
                                )
                                Text("點一下查看各題秒數", style = MaterialTheme.typography.bodySmall)
                            }
                            Icon(
                                if (showQuestionTimes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }
                    }
                }

                if (showQuestionTimes) {
                    itemsIndexed(session.answers) { index, answer ->
                        Card {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("第 ${index + 1} 題 ${if (answer.isCorrect) "✓" else "✗"}")
                                Text(String.format("%.1f 秒", answer.elapsedMillis / 1000.0), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Surface(shadowElevation = 8.dp) {
            Column(
                Modifier.fillMaxWidth().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Button(onClick = onHome, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Home, null)
                    Spacer(Modifier.width(8.dp))
                    Text("返回主選單")
                }
                OutlinedButton(onClick = onAnalytics, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Analytics, null)
                    Spacer(Modifier.width(8.dp))
                    Text("查看完整答題分析")
                }
            }
        }
    }
}

@Composable
private fun ScoreBlock(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

private fun encouragement(score: Int): String = when {
    score >= 90 -> "太厲害了！黑糖、偶貴、熊熊都替你開心！ 🎉"
    score >= 70 -> "表現很不錯，再複習一下就更強了！ 🐾"
    score >= 50 -> "有進步就是好事，一起把錯題再練一次！ 💪"
    else -> "黑糖、偶貴、熊熊陪你慢慢練習！ 🌟"
}

package com.petlingo.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.QuizSession
import com.petlingo.app.ui.components.AnswerTimeChart
import com.petlingo.app.ui.components.RadarChart
import com.petlingo.app.util.AnalyticsCalculator

@Composable
fun AnalyticsScreen(session: QuizSession?) {
    val metrics = AnalyticsCalculator.metrics(session)
    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("答題分析", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        if (session == null) {
            item { Text("完成一次測驗後即可查看分析。") }
            return@LazyColumn
        }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${session.modeLabel}・${session.score} 分", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    SummaryLine("答對／題數", "${session.correctCount}/${session.questionCount}")
                    SummaryLine("總作答時間", AnalyticsCalculator.totalTime(session.totalMillis))
                    SummaryLine("平均每題", AnalyticsCalculator.seconds(AnalyticsCalculator.averageMillis(session.answers)))
                    SummaryLine("答對題平均", AnalyticsCalculator.seconds(AnalyticsCalculator.averageCorrectMillis(session)))
                    SummaryLine("答錯題平均", AnalyticsCalculator.seconds(AnalyticsCalculator.averageWrongMillis(session)))
                    SummaryLine("修改答案", "${AnalyticsCalculator.changedCount(session)} 題")
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Metric("作答速度", metrics.speed, "依本次每題耗時計算")
                    Metric("答題準確力", metrics.accuracy, "本次測驗正確率")
                    Metric("單字能力", metrics.vocabulary, "本次單字題表現")
                    Metric("文法能力", metrics.grammar, "本次文法題表現")
                    Metric("閱讀能力", metrics.reading, "本次閱讀題表現")
                    Metric("聽力能力", metrics.listening, "本次聽力題表現")
                }
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
            Text("作答時間曲線", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("綠點為答對，紅點為答錯；曲線越高代表該題花費越久。", style = MaterialTheme.typography.bodySmall)
            AnswerTimeChart(session.answers)
        }

        item {
            val fastest = AnalyticsCalculator.fastest(session)
            val slowest = AnalyticsCalculator.slowest(session)
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("速度重點", fontWeight = FontWeight.Bold)
                    Text("最快：${fastest?.prompt.orEmpty()}・${AnalyticsCalculator.seconds(fastest?.elapsedMillis ?: 0L)}")
                    Text("最慢：${slowest?.prompt.orEmpty()}・${AnalyticsCalculator.seconds(slowest?.elapsedMillis ?: 0L)}")
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("學習建議", fontWeight = FontWeight.Bold)
                    Text(AnalyticsCalculator.insight(session))
                }
            }
        }

        item { Text("逐題分析", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        itemsIndexed(session.answers) { index, answer ->
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Q${index + 1} ${if (answer.isCorrect) "答對" else "答錯"}", fontWeight = FontWeight.Bold)
                    Text(answer.prompt)
                    Text("作答時間：${AnalyticsCalculator.seconds(answer.elapsedMillis)}")
                    Text("答案：${answer.selectedAnswer} → ${answer.correctAnswer}")
                    Text("修改答案：${if (answer.changedAnswer) "是" else "否"}")
                    if (answer.explanation.isNotBlank()) Text("解析：${answer.explanation}")
                }
            }
        }
    }
}

@Composable
private fun Metric(name: String, value: Float, description: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(name, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
        Text("%.1f / 5".format(value))
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

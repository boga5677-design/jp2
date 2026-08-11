package com.petlingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.QuizMode

@Composable
fun QuizSetupScreen(
    favoriteCount: Int,
    hasActiveQuiz: Boolean,
    onResume: () -> Unit,
    onStart: (Int, QuizMode) -> Boolean,
    onReady: () -> Unit
) {
    var count by remember { mutableIntStateOf(20) }
    var mode by remember { mutableStateOf(QuizMode.ENGLISH_TO_CHINESE) }
    var warning by remember { mutableStateOf<String?>(null) }

    val modes = listOf(
        QuizMode.ENGLISH_TO_CHINESE,
        QuizMode.CHINESE_TO_ENGLISH,
        QuizMode.PHRASE,
        QuizMode.FAVORITES
    )

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("測驗", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        if (hasActiveQuiz) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Text("有尚未完成的測驗", fontWeight = FontWeight.Bold)
                    Text("進度已記憶，可直接從離開前的位置繼續。", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onResume, modifier = Modifier.fillMaxWidth()) {
                        Text("繼續未完成測驗")
                    }
                }
            }
        }

        Text("題型", fontWeight = FontWeight.Bold)

        modes.forEach { item ->
            val description = when (item) {
                QuizMode.ENGLISH_TO_CHINESE -> "看到日文單字，選擇正確中文。"
                QuizMode.CHINESE_TO_ENGLISH -> "看到中文意思，選擇正確日文。"
                QuizMode.PHRASE -> "常用日文語句、中文意思與例句。"
                QuizMode.CLOZE -> "文法、介系詞與固定搭配克漏字。"
                QuizMode.READING -> "公告、郵件、廣告與短篇文章閱讀。"
                QuizMode.FAVORITES -> "只使用收藏單字，目前共 $favoriteCount 個。"
                QuizMode.TOEIC_MOCK -> "單字、片語、克漏字與閱讀混合題。"
                else -> ""
            }

            Card(
                onClick = { mode = item; warning = null },
                colors = CardDefaults.cardColors(
                    containerColor = if (mode == item) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.fillMaxWidth().padding(12.dp)) {
                    RadioButton(selected = mode == item, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(item.label, fontWeight = FontWeight.Bold)
                        Text(description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Text("題數", fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10, 20, 40).forEach { n ->
                FilterChip(
                    selected = count == n,
                    onClick = { count = n },
                    label = { Text("$n 題") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        warning?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = {
                if (onStart(count, mode)) onReady()
                else warning = if (mode == QuizMode.FAVORITES) "請先收藏至少 4 個單字。" else "題庫不足，無法建立測驗。"
            },
            modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)
        ) {
            Text("開始測驗")
        }
    }
}

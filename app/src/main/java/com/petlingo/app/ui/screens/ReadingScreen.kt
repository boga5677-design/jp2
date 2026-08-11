package com.petlingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.ReadingPassage

@Composable
fun ReadingScreen(passages: List<ReadingPassage>, onOpen: (Int) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("TOEIC 閱讀練習", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item { Text("每篇閱讀獨立作答並記錄每題時間；答錯題目會加入錯題本。") }
        items(passages) { passage ->
            Card(onClick = { onOpen(passage.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(passage.category, color = MaterialTheme.colorScheme.primary)
                    Text(passage.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${passage.questions.size} 題")
                }
            }
        }
    }
}

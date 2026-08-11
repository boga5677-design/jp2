package com.petlingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LearningScreen(
    onVocabulary: () -> Unit,
    onPhrase: () -> Unit,
    onListeningPractice: () -> Unit,
    onSpeakingPractice: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("學習", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("日文單字、實用語句、聽力與口說練習集中在這裡。")

        LearningCard("日文單字學習", "JLPT N5・N4 基礎", Icons.Default.MenuBook, onVocabulary)
        LearningCard("實用語句", "生活・旅行常用句", Icons.Default.Chat, onPhrase)
        LearningCard("聽力練習", "聽單字發音，再確認中文意思", Icons.Default.Headphones, onListeningPractice)
        LearningCard("口說練習", "單字與片語隨機跟讀練習", Icons.Default.Mic, onSpeakingPractice)
    }
}

@Composable
private fun LearningCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

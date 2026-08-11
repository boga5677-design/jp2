package com.petlingo.app.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.StudyNote
import com.petlingo.app.model.Word
import java.util.Locale

@Composable
fun VocabularyScreen(
    words: List<Word>,
    query: String,
    favorites: Set<Int>,
    noteKeys: Set<String>,
    onQueryChange: (String) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onToggleNote: (StudyNote) -> Unit
) {
    var selectedLevel by remember { mutableStateOf("全部") }
    var selectedCategory by remember { mutableStateOf("全部分類") }
    val context = LocalContext.current
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember { TextToSpeech(context) { ttsReady = it == TextToSpeech.SUCCESS } }
    DisposableEffect(tts) { onDispose { tts.stop(); tts.shutdown() } }

    val categories = remember(words) { listOf("全部分類") + words.map { it.academic }.filter { it.isNotBlank() }.distinct().sorted() }
    val shown = remember(words, selectedLevel, selectedCategory) {
        words.filter { word ->
            (selectedLevel == "全部" || word.level == selectedLevel) &&
            (selectedCategory == "全部分類" || word.academic == selectedCategory)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("日文單字庫", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("共 ${shown.size} 筆・已收藏 ${favorites.size} 個")
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("搜尋日文、假名、羅馬字或中文") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("全部", "N5", "N4")) { level ->
                FilterChip(selected = selectedLevel == level, onClick = { selectedLevel = level }, label = { Text(level) })
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                FilterChip(selected = selectedCategory == category, onClick = { selectedCategory = category }, label = { Text(category) })
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(shown, key = { it.id }) { word ->
                val noteKey = "vocab-${word.id}"
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(word.english, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                if (word.partOfSpeech.isNotBlank()) {
                                    Spacer(Modifier.width(8.dp))
                                    Text(word.partOfSpeech, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            if (word.note.isNotBlank()) Text(word.note, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                            Text(word.chinese)
                            Text(
                                buildString {
                                    append(word.level)
                                    if (word.academic.isNotBlank()) append("・${word.academic}")
                                    if (word.ceecLevel.isNotBlank()) append("・第 ${word.ceecLevel} 組")
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                if (ttsReady) {
                                    tts.language = Locale.JAPAN
                                    tts.setSpeechRate(0.85f)
                                    tts.speak(word.english, TextToSpeech.QUEUE_FLUSH, null, "jp-word-${word.id}")
                                }
                            }) { Icon(Icons.Default.VolumeUp, contentDescription = "播放日文發音") }
                            IconButton(onClick = { onToggleFavorite(word.id) }) {
                                Icon(if (word.id in favorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "收藏")
                            }
                            IconButton(onClick = {
                                onToggleNote(StudyNote(noteKey, "日文單字", "單字", word.english, "${word.note}
${word.chinese}", "${word.level}・${word.academic}"))
                            }) {
                                Icon(if (noteKey in noteKeys) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = "筆記")
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.petlingo.app.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.Phrase
import java.util.Locale

@Composable
fun PhraseScreen(phrases: List<Phrase>) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val engine = TextToSpeech(context) { if (it == TextToSpeech.SUCCESS) Unit }
        tts = engine
        onDispose { engine.stop(); engine.shutdown() }
    }
    val shown = remember(query, phrases) {
        if (query.isBlank()) phrases else phrases.filter { it.english.contains(query, true) || it.chinese.contains(query) }
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("日文實用語句", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("常用生活與旅遊日語，共 ${phrases.size} 組")
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), label = { Text("搜尋日文或中文") }, singleLine = true)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(shown, key = { it.id }) { phrase ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(phrase.english, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { tts?.language = Locale.JAPAN; tts?.speak(phrase.english, TextToSpeech.QUEUE_FLUSH, null, "phrase-${phrase.id}") }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "播放發音")
                            }
                        }
                        Text(phrase.chinese, color = MaterialTheme.colorScheme.primary)
                        HorizontalDivider()
                        Text(phrase.example)
                    }
                }
            }
        }
    }
}

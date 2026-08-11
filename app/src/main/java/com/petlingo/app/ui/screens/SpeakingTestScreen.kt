package com.petlingo.app.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.petlingo.app.model.Phrase
import com.petlingo.app.model.SpeakingRecord
import com.petlingo.app.model.Word
import com.petlingo.app.util.SpeakingScorer
import java.util.Locale

@Composable
fun SpeakingTestScreen(
    words: List<Word>,
    phrases: List<Phrase>,
    onSave: (SpeakingRecord) -> Unit
) {
    val context = LocalContext.current
    var target by remember(words, phrases) {
        mutableStateOf(listOfNotNull(words.randomOrNull()?.english, phrases.randomOrNull()?.english).randomOrNull().orEmpty())
    }
    var recognized by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(-1) }
    var accent by remember { mutableStateOf("日語") }
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember { TextToSpeech(context) { ttsReady = it == TextToSpeech.SUCCESS } }

    fun nextTarget() {
        val old = target
        val pool = buildList {
            addAll(words.shuffled().take(100).map { it.english })
            addAll(phrases.map { it.english })
        }.filter { it.isNotBlank() && it != old }
        target = pool.randomOrNull().orEmpty()
        recognized = ""
        score = -1
    }

    DisposableEffect(tts) { onDispose { tts.stop(); tts.shutdown() } }
    LaunchedEffect(accent, ttsReady) {
        if (ttsReady) tts.language = Locale.JAPAN
    }

    fun acceptResult(text: String) {
        recognized = text
        score = SpeakingScorer.score(target, text)
        onSave(SpeakingRecord(targetText = target, recognizedText = text, score = score, accent = accent))
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            acceptResult(
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            )
        }
    }

    fun startRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "請朗讀：$target")
        }
        launcher.launch(intent)
    }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startRecognition()
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("口說測驗", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(target, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)

        AssistChip(onClick = {}, label = { Text("日語・日本") })

        Button(
            onClick = {
                if (ttsReady) tts.speak(target, TextToSpeech.QUEUE_FLUSH, null, "speaking-test")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.VolumeUp, null)
            Spacer(Modifier.width(8.dp))
            Text("播放示範")
        }

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
                ) startRecognition()
                else permission.launch(Manifest.permission.RECORD_AUDIO)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Mic, null)
            Spacer(Modifier.width(8.dp))
            Text("開始口說測驗")
        }

        if (recognized.isNotBlank()) Text("辨識：$recognized")
        if (score >= 0) Text("本次分數：$score 分", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        OutlinedButton(onClick = ::nextTarget, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Shuffle, null)
            Spacer(Modifier.width(8.dp))
            Text("下一題")
        }
    }
}

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SpeakingScreen(
    words: List<Word>,
    phrases: List<Phrase>,
    records: List<SpeakingRecord>,
    onSave: (SpeakingRecord) -> Unit,
    onClear: () -> Unit
) {
    val context = LocalContext.current
    var mode by remember { mutableStateOf("混合") }
    var target by remember(words, phrases) {
        mutableStateOf(words.randomOrNull()?.english ?: phrases.randomOrNull()?.english.orEmpty())
    }
    var recognized by remember { mutableStateOf("") }
    var accent by remember { mutableStateOf("日語") }
    var score by remember { mutableIntStateOf(-1) }
    var status by remember { mutableStateOf("題目會從單字表或片語題庫隨機抽出。") }
    var listMode by remember { mutableStateOf("單字表") }
    var listQuery by remember { mutableStateOf("") }
    var ttsReady by remember { mutableStateOf(false) }
    val tts = remember { TextToSpeech(context) { ttsReady = it == TextToSpeech.SUCCESS } }

    fun drawPrompt(): String = when (mode) {
        "單字" -> words.randomOrNull()?.english
        "片語" -> phrases.randomOrNull()?.english
        else -> listOfNotNull(words.randomOrNull()?.english, phrases.randomOrNull()?.english).randomOrNull()
    }.orEmpty()

    fun nextPrompt() {
        val old = target
        var next = drawPrompt()
        repeat(8) {
            if (next.isNotBlank() && next != old) return@repeat
            next = drawPrompt()
        }
        target = next
        recognized = ""
        score = -1
        status = "新題目已抽出，先聽示範再跟讀。"
    }

    DisposableEffect(Unit) { onDispose { tts.stop(); tts.shutdown() } }
    LaunchedEffect(accent, ttsReady) {
        if (ttsReady) tts.language = Locale.JAPAN
    }
    LaunchedEffect(mode) { nextPrompt() }

    fun saveResult(text: String) {
        recognized = text
        score = SpeakingScorer.score(target, text)
        status = SpeakingScorer.feedback(target, text, score)
        onSave(SpeakingRecord(targetText = target, recognizedText = text, score = score, accent = accent))
    }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            saveResult(
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull().orEmpty()
            )
        } else status = "未取得語音辨識結果，可再試一次。"
    }

    fun startRecognition() {
        val locale = "ja-JP"
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "請朗讀：$target")
        }
        runCatching { speechLauncher.launch(intent) }
            .onFailure { status = "裝置目前沒有可用的語音辨識服務。" }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startRecognition() else status = "需要麥克風權限才能進行口說辨識。"
    }

    LazyColumn(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("口說練習", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("從 ${words.size} 筆單字與 ${phrases.size} 組片語中抽題。")
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("混合", "單字", "片語").forEach { item ->
                    FilterChip(selected = mode == item, onClick = { mode = item }, label = { Text(item) })
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(target.ifBlank { "題庫載入中" }, Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = true, onClick = { accent = "日語" }, label = { Text("日語・日本") })
                        
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (target.isNotBlank() && ttsReady) {
                                    tts.speak(target, TextToSpeech.QUEUE_FLUSH, null, "petlingo-demo")
                                } else status = "語音示範尚未準備完成。"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.VolumeUp, null)
                            Text("示範")
                        }
                        Button(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) startRecognition()
                                else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Mic, null)
                            Text("跟讀")
                        }
                    }
                    OutlinedButton(onClick = ::nextPrompt, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Shuffle, null)
                        Spacer(Modifier.width(6.dp))
                        Text("隨機下一題")
                    }
                    if (recognized.isNotBlank()) Text("辨識結果：$recognized")
                    if (score >= 0) Text("本次相似度：$score 分",
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(status)
                }
            }
        }
        item {
            HorizontalDivider()
            Text("口說練習單字／片語表", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = listMode == "單字表", onClick = { listMode = "單字表" }, label = { Text("單字表") })
                FilterChip(selected = listMode == "片語表", onClick = { listMode = "片語表" }, label = { Text("片語表") })
            }
            OutlinedTextField(value=listQuery,onValueChange={listQuery=it},label={Text("搜尋日文、假名或中文")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        }
        if (listMode == "單字表") {
            val shownWords=words.filter{listQuery.isBlank()||it.english.contains(listQuery,true)||it.chinese.contains(listQuery)}.take(200)
            items(shownWords,key={"word-${it.id}"}){word->Card(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text(word.english,fontWeight=FontWeight.Bold);Text("${word.chinese}・${word.level}",style=MaterialTheme.typography.bodySmall)};TextButton({target=word.english;recognized="";score=-1}){Text("練習")}}}}
        } else {
            val shownPhrases=phrases.filter{listQuery.isBlank()||it.english.contains(listQuery,true)||it.chinese.contains(listQuery)}
            items(shownPhrases,key={"phrase-${it.id}"}){phrase->Card(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text(phrase.english,fontWeight=FontWeight.Bold);Text(phrase.chinese,style=MaterialTheme.typography.bodySmall);Text(phrase.example,style=MaterialTheme.typography.labelSmall)};TextButton({target=phrase.english;recognized="";score=-1}){Text("練習")}}}}
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("最近口說紀錄", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (records.isNotEmpty()) IconButton(onClick = onClear) {
                    Icon(Icons.Default.Delete, "清除口說紀錄")
                }
            }
        }
        if (records.isEmpty()) item { Text("尚無口說紀錄。") }
        items(records.take(30), key = { it.id }) { record ->
            val date = remember(record.createdAt) {
                SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date(record.createdAt))
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(record.targetText, fontWeight = FontWeight.Bold)
                    Text("辨識：${record.recognizedText.ifBlank { "無結果" }}")
                    Text("${record.accent}・${record.score} 分・$date")
                }
            }
        }
    }
}

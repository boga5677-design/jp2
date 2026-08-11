package com.petlingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.QuizMode

@Composable
fun ListeningSetupScreen(
    onStart: (Int, QuizMode) -> Boolean,
    onReady: () -> Unit,
    hasActiveQuiz: Boolean,
    onResume: () -> Unit
) {
    var count by remember { mutableIntStateOf(20) }
    var warning by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(Icons.Default.Headphones, null, modifier = Modifier.size(62.dp), tint = MaterialTheme.colorScheme.primary)
        Text("聽力測驗", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        if (hasActiveQuiz) {
            FilledTonalButton(onClick = onResume, modifier = Modifier.fillMaxWidth()) {
                Text("繼續未完成測驗")
            }
        }

        Text("題數", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10, 20, 40).forEach { option ->
                FilterChip(
                    selected = count == option,
                    onClick = { count = option },
                    label = { Text("$option 題") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        warning?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = {
                if (onStart(count, QuizMode.LISTENING)) onReady()
                else warning = "題庫不足，無法建立聽力測驗。"
            },
            modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp)
        ) {
            Text("開始聽力測驗")
        }
    }
}

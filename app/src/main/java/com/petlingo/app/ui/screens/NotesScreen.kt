package com.petlingo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.StudyNote

@Composable
fun NotesScreen(
    notes: List<StudyNote>,
    onBack: () -> Unit,
    onRemove: (String) -> Unit,
    onClear: () -> Unit
) {
    val categories = remember(notes) {
        listOf("全部", "單字庫") + notes
            .map { it.category }
            .filter { it != "單字庫" }
            .distinct()
            .sorted()
    }
    var selectedCategory by remember { mutableStateOf("全部") }

    val shown = remember(notes, selectedCategory) {
        if (selectedCategory == "全部") notes
        else notes.filter { it.category == selectedCategory }
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "返回")
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "我的筆記",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "用 ★ 收藏題目、選項與單字",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (notes.isNotEmpty()) {
                TextButton(onClick = onClear) {
                    Text("全部清除")
                }
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) }
                )
            }
        }

        if (shown.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("這個分類目前還沒有筆記。")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shown, key = { it.key }) { note ->
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        note.category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        note.kind,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                IconButton(onClick = { onRemove(note.key) }) {
                                    Icon(Icons.Default.Delete, "刪除筆記")
                                }
                            }

                            Text(
                                note.title,
                                fontWeight = FontWeight.Bold
                            )

                            if (note.content.isNotBlank()) {
                                Text(note.content)
                            }

                            if (note.detail.isNotBlank()) {
                                Text(
                                    note.detail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

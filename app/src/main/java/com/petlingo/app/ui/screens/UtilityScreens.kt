package com.petlingo.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petlingo.app.R
import com.petlingo.app.data.AppSettings
import com.petlingo.app.model.Word

@Composable
fun FavoritesScreen(
    words: List<Word>,
    favorites: Set<Int>,
    onToggle: (Int) -> Unit
) {
    val favoriteWords = words.filter { it.id in favorites }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "我的收藏",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "收藏的單字可用於專屬測驗。",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (favoriteWords.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "目前尚未收藏單字。",
                        modifier = Modifier.padding(18.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(
            items = favoriteWords,
            key = { it.id }
        ) { word ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = word.english,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = word.chinese,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (word.level.isNotBlank()) {
                            Text(
                                text = word.level,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            onToggle(word.id)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "取消收藏",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyMissionScreen(
    answered: Int,
    onStartQuiz: () -> Unit
) {
    val progress = answered.coerceIn(0, 20)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "每日任務",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "成人版每天完成 20 題即可獲得獎勵。",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "$progress / 20 題",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                LinearProgressIndicator(
                    progress = {
                        progress / 20f
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = when {
                        progress >= 20 ->
                            "今日任務已完成！"

                        progress == 0 ->
                            "今天尚未開始作答。"

                        else ->
                            "再完成 ${20 - progress} 題即可完成今日任務。"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = onStartQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp)
        ) {
            Text(
                text = if (progress >= 20) {
                    "再挑戰一次"
                } else {
                    "開始 20 題測驗"
                }
            )
        }
    }
}

@Composable
fun AchievementsScreen(
    sessionCount: Int,
    answered: Int
) {
    val achievements = listOf(
        AchievementItem("初次見面", "完成第一個學習任務", sessionCount >= 1, R.drawable.chihuahua, Color(0xFFE86B52)),
        AchievementItem("專注學習", "完成 5 次測驗", sessionCount >= 5, R.drawable.tabby, Color(0xFF1684E8)),
        AchievementItem("持之以恆", "今日完成 20 題", answered >= 20, R.drawable.chihuahua, Color(0xFF5E9D48)),
        AchievementItem("測驗新手", "完成第一個測驗", sessionCount >= 1, R.drawable.tortoiseshell, Color(0xFF7C4DCE)),
        AchievementItem("測驗達人", "完成 20 次測驗", sessionCount >= 20, R.drawable.tabby, Color(0xFF1684E8)),
        AchievementItem("全對高手", "累積完成 30 次測驗", sessionCount >= 30, R.drawable.tortoiseshell, Color(0xFFE34F65)),
        AchievementItem("速度之星", "累積完成 40 次測驗", sessionCount >= 40, R.drawable.chihuahua, Color(0xFFF39A22)),
        AchievementItem("聽力專家", "累積完成 50 次測驗", sessionCount >= 50, R.drawable.tabby, Color(0xFF5E9D48)),
        AchievementItem("口說小能手", "累積完成 60 次測驗", sessionCount >= 60, R.drawable.chihuahua, Color(0xFF1684E8)),
        AchievementItem("文法達人", "累積完成 70 次測驗", sessionCount >= 70, R.drawable.tabby, Color(0xFFE34F65)),
        AchievementItem("閱讀高手", "累積完成 80 次測驗", sessionCount >= 80, R.drawable.chihuahua, Color(0xFF7C4DCE)),
        AchievementItem("筆記達人", "累積完成 90 次測驗", sessionCount >= 90, R.drawable.tortoiseshell, Color(0xFFF39A22)),
        AchievementItem("完美主義", "累積完成 100 次測驗", sessionCount >= 100, R.drawable.tabby, Color(0xFF5E9D48)),
        AchievementItem("學習狂人", "累積完成 120 次測驗", sessionCount >= 120, R.drawable.tortoiseshell, Color(0xFF1684E8)),
        AchievementItem("三毛夥伴", "累積完成 150 次測驗", sessionCount >= 150, R.drawable.chihuahua, Color(0xFF7C4DCE))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFFFE6B8)) {
                    Text("🐾  成就徽章  🐾", modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color(0xFF4B2F20))
                }
                Spacer(Modifier.height(8.dp))
                Text("和三隻毛孩一起解鎖學習里程碑", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        items(achievements.chunked(3)) { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { achievement ->
                    AchievementBadge(achievement, Modifier.weight(1f))
                }
                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun AchievementBadge(item: AchievementItem, modifier: Modifier = Modifier) {
    val alpha = if (item.unlocked) 1f else 0.42f
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF3))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(contentAlignment = Alignment.TopCenter) {
                Surface(
                    modifier = Modifier.size(82.dp),
                    shape = CircleShape,
                    color = item.accent.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(4.dp, item.accent.copy(alpha = alpha))
                ) {
                    Image(
                        painter = painterResource(item.petRes),
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        alpha = alpha
                    )
                }
                Text(if (item.unlocked) "⭐" else "🔒", modifier = Modifier.offset(y = (-10).dp))
            }
            Surface(shape = RoundedCornerShape(50), color = item.accent.copy(alpha = alpha)) {
                Text(item.title, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            }
            Text(item.description, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                minLines = 2, maxLines = 2)
            Text(if (item.unlocked) "已解鎖" else "尚未解鎖",
                style = MaterialTheme.typography.labelSmall,
                color = if (item.unlocked) item.accent else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 18.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "返回首頁")
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "設定",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "調整發音、測驗、顯示與學習偏好。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            SettingsSection(
                title = "發音與聲音",
                icon = Icons.Default.VolumeUp
            ) {
                Text(
                    text = "預設發音",
                    fontWeight = FontWeight.Bold
                )

                ChoiceRow(
                    choices = listOf(
                        "日語"
                    ),
                    selected = settings.accent,
                    onSelected = { value ->
                        onUpdate(
                            settings.copy(
                                accent = value
                            )
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "語音速度：${
                        String.format(
                            "%.2f",
                            settings.speechRate
                        )
                    }×"
                )

                Slider(
                    value = settings.speechRate,
                    onValueChange = { value ->
                        onUpdate(
                            settings.copy(
                                speechRate = value
                            )
                        )
                    },
                    valueRange = 0.70f..1.30f,
                    steps = 5
                )

                SettingSwitch(
                    title = "按鈕與答題音效",
                    description = "播放介面操作、答對與答錯提示音。",
                    checked = settings.soundEffects,
                    onCheckedChange = { checked ->
                        onUpdate(
                            settings.copy(
                                soundEffects = checked
                            )
                        )
                    }
                )

                SettingSwitch(
                    title = "題目自動朗讀",
                    description = "進入單字或聽力題目時，自動播放日文。",
                    checked = settings.autoReadQuestion,
                    onCheckedChange = { checked ->
                        onUpdate(
                            settings.copy(
                                autoReadQuestion = checked
                            )
                        )
                    }
                )
            }
        }

        item {
            SettingsSection(
                title = "測驗與學習",
                icon = Icons.Default.Quiz
            ) {
                Text(
                    text = "預設題數",
                    fontWeight = FontWeight.Bold
                )

                ChoiceRow(
                    choices = listOf(
                        "10",
                        "20",
                        "40"
                    ),
                    selected =
                        settings.defaultQuestionCount.toString(),
                    onSelected = { value ->
                        onUpdate(
                            settings.copy(
                                defaultQuestionCount =
                                    value.toInt()
                            )
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "預設單字級數",
                    fontWeight = FontWeight.Bold
                )

                ChoiceRow(
                    choices = listOf(
                        "全部",
                        "初級",
                        "中級",
                        "中高級"
                    ),
                    selected = settings.defaultLevel,
                    onSelected = { value ->
                        onUpdate(
                            settings.copy(
                                defaultLevel = value
                            )
                        )
                    }
                )

                SettingSwitch(
                    title = "答題後顯示解析",
                    description = "顯示正確答案、中文意思與題目說明。",
                    checked = settings.showExplanation,
                    onCheckedChange = { checked ->
                        onUpdate(
                            settings.copy(
                                showExplanation = checked
                            )
                        )
                    }
                )

                SettingSwitch(
                    title = "錯題自動加入錯題本",
                    description = "答錯後自動保存，方便之後複習。",
                    checked =
                        settings.addWrongAnswerAutomatically,
                    onCheckedChange = { checked ->
                        onUpdate(
                            settings.copy(
                                addWrongAnswerAutomatically =
                                    checked
                            )
                        )
                    }
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "每日任務目標",
                    fontWeight = FontWeight.Bold
                )

                ChoiceRow(
                    choices = listOf(
                        "10",
                        "20",
                        "40"
                    ),
                    selected =
                        settings.dailyGoal.toString(),
                    onSelected = { value ->
                        onUpdate(
                            settings.copy(
                                dailyGoal =
                                    value.toInt()
                            )
                        )
                    }
                )
            }
        }

        item {
            SettingsSection(
                title = "顯示與提醒",
                icon = Icons.Default.Palette
            ) {
                Text(
                    text = "顯示模式",
                    fontWeight = FontWeight.Bold
                )

                ChoiceRow(
                    choices = listOf(
                        "系統",
                        "淺色",
                        "深色"
                    ),
                    selected = settings.themeMode,
                    onSelected = { value ->
                        onUpdate(
                            settings.copy(
                                themeMode = value
                            )
                        )
                    }
                )

                SettingSwitch(
                    title = "大型文字",
                    description = "增加設定與學習頁面的文字可讀性。",
                    checked = settings.largeText,
                    onCheckedChange = { checked ->
                        onUpdate(
                            settings.copy(
                                largeText = checked
                            )
                        )
                    }
                )

                SettingSwitch(
                    title = "每日學習提醒",
                    description = "保存每日學習提醒偏好。",
                    checked = settings.dailyReminder,
                    onCheckedChange = { checked ->
                        onUpdate(
                            settings.copy(
                                dailyReminder = checked
                            )
                        )
                    }
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            content()
        }
    }
}

@Composable
private fun ChoiceRow(
    choices: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {
        choices.forEach { value ->
            FilterChip(
                selected = selected == value,
                onClick = {
                    onSelected(value)
                },
                label = {
                    Text(
                        text = value,
                        maxLines = 1
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = description,
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private data class AchievementItem(
    val title: String,
    val description: String,
    val unlocked: Boolean,
    val petRes: Int,
    val accent: Color
)

package com.petlingo.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.petlingo.app.R
import com.petlingo.app.model.QuizSession
import com.petlingo.app.util.AnalyticsCalculator

private val HomeTitleColor = Color(0xFF2B2B2B)
private val HomeSubtitleColor = Color(0xFF555555)
private val HomeSecondaryColor = Color(0xFF666666)
private val HomeIconColor = Color(0xFF7C4DFF)
private val HomeGreenColor = Color(0xFF3B8B56)
private val HomeRewardColor = Color(0xFFD89224)

@Composable
fun HomeScreen(
    last: QuizSession?,
    favoriteCount: Int,
    todayAnswered: Int,
    onQuiz: () -> Unit,
    onVocabulary: () -> Unit,
    onPhrase: () -> Unit,
    onMock: () -> Unit,
    onAnalytics: () -> Unit,
    onHistory: () -> Unit,
    onReading: () -> Unit,
    onWrongAnswers: () -> Unit,
    onSpeaking: () -> Unit,
    onListening: () -> Unit,
    onFavorites: () -> Unit,
    onDailyMission: () -> Unit,
    onAchievements: () -> Unit,
    onNotes: () -> Unit,
    onSettings: () -> Unit
) {
    val progress = todayAnswered.coerceIn(0, 20)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            horizontal = 14.dp,
            vertical = 10.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onSettings) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "選單"
                    )
                }

                Text(
                    text = "Nihongo Go",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Hi, 學習者 👋",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "持續學習的每一天都很棒！",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onSettings) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "通知"
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.petlingo_home_banner),
                    contentDescription = "一起學日文",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.78f),
                    contentScale = ContentScale.Crop
                )
            }
        }

        item {
            Card(
                onClick = onDailyMission,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF0D5)
                ),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(22.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = HomeRewardColor,
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "今日任務（成人版）",
                                color = HomeTitleColor,
                                fontWeight = FontWeight.Black
                            )

                            Text(
                                text = "完成 20 題可獲得獎勵！",
                                style = MaterialTheme.typography.bodySmall,
                                color = HomeSubtitleColor
                            )
                        }

                        Text(
                            text = "$progress / 20",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = HomeIconColor
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress / 20f },
                        modifier = Modifier.fillMaxWidth(),
                        color = HomeIconColor,
                        trackColor = Color(0xFFE5D7FF)
                    )
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeatureRow(
                    a = HomeFeature(
                        title = "日文單字庫",
                        subtitle = "N5・N4 基礎單字",
                        icon = Icons.Default.MenuBook,
                        color = Color(0xFFE8F2D9),
                        action = onVocabulary
                    ),
                    b = HomeFeature(
                        title = "實用語句",
                        subtitle = "生活・旅遊常用句",
                        icon = Icons.Default.Chat,
                        color = Color(0xFFFFE5E0),
                        action = onPhrase
                    )
                )

                FeatureRow(
                    a = HomeFeature(
                        title = "單字測驗",
                        subtitle = "日文↔中文雙向練習",
                        icon = Icons.Default.Quiz,
                        color = Color(0xFFFFF0D5),
                        action = onQuiz
                    ),
                    b = HomeFeature(
                        title = "收藏複習",
                        subtitle = "只練習已收藏單字",
                        icon = Icons.Default.Favorite,
                        color = Color(0xFFEDE5FA),
                        action = onFavorites
                    )
                )

                FeatureRow(
                    a = HomeFeature(
                        title = "聽力測驗",
                        subtitle = "播放日文・選出中文",
                        icon = Icons.Default.Headphones,
                        color = Color(0xFFE1EDFA),
                        action = onListening
                    ),
                    b = HomeFeature(
                        title = "口說練習",
                        subtitle = "隨機抽題・單字片語表",
                        icon = Icons.Default.Mic,
                        color = Color(0xFFEDE5FA),
                        action = onSpeaking
                    )
                )

                FeatureRow(
                    a = HomeFeature(
                        title = "學習分析",
                        subtitle = "答題時間・弱點分析",
                        icon = Icons.Default.Analytics,
                        color = Color(0xFFDDF3EE),
                        action = onAnalytics
                    ),
                    b = HomeFeature(
                        title = "錯題本",
                        subtitle = "錯題複習・加強記憶",
                        icon = Icons.Default.EditNote,
                        color = Color(0xFFFFE4DE),
                        action = onWrongAnswers
                    )
                )

                FeatureRow(
                    a = HomeFeature(
                        title = "成就與獎勵",
                        subtitle = "累積徽章・兌換獎勵",
                        icon = Icons.Default.EmojiEvents,
                        color = Color(0xFFFFF0C8),
                        action = onAchievements
                    ),
                    b = HomeFeature(
                        title = "測驗設定",
                        subtitle = "10／20／40 題",
                        icon = Icons.Default.Quiz,
                        color = Color(0xFFE8F2D9),
                        action = onQuiz
                    )
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ToolCard(
                    title = "我的收藏",
                    subtitle = "$favoriteCount 個",
                    icon = Icons.Default.Favorite,
                    action = onFavorites,
                    modifier = Modifier.weight(1f)
                )

                ToolCard(
                    title = "每日任務",
                    subtitle = "挑戰 20 題",
                    icon = Icons.Default.Today,
                    action = onDailyMission,
                    modifier = Modifier.weight(1f)
                )

                ToolCard(
                    title = "我的筆記",
                    subtitle = "★ 題目・單字",
                    icon = Icons.Default.Star,
                    action = onNotes,
                    modifier = Modifier.weight(1f)
                )

                ToolCard(
                    title = "學習紀錄",
                    subtitle = "查看歷次紀錄",
                    icon = Icons.Default.History,
                    action = onHistory,
                    modifier = Modifier.weight(1f)
                )

                ToolCard(
                    title = "設定",
                    subtitle = "音效・顯示",
                    icon = Icons.Default.Settings,
                    action = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F8F8)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = "最近學習",
                            color = HomeTitleColor,
                            fontWeight = FontWeight.Bold
                        )

                        if (last == null) {
                            Text(
                                text = "尚未完成測驗",
                                color = HomeSubtitleColor
                            )
                        } else {
                            Text(
                                text = last.modeLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = HomeSubtitleColor
                            )

                            Text(
                                text = "${last.score} 分・正確 ${last.correctCount} 題",
                                color = HomeGreenColor,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = AnalyticsCalculator.totalTime(
                                    last.totalMillis
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = HomeSecondaryColor
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF7E8)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = "今日建議",
                            color = HomeTitleColor,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "建議複習",
                            style = MaterialTheme.typography.bodySmall,
                            color = HomeSubtitleColor
                        )

                        Text(
                            text = "cooperation",
                            color = HomeTitleColor,
                            fontWeight = FontWeight.Black
                        )

                        Button(
                            onClick = onPhrase,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("立即複習")
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F8F8)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "連續學習天數",
                            color = HomeTitleColor,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "7 天",
                            style = MaterialTheme.typography.headlineMedium,
                            color = HomeTitleColor,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = "太棒了！繼續加油！",
                            style = MaterialTheme.typography.bodySmall,
                            color = HomeSubtitleColor
                        )
                    }

                    Text(
                        text = "🔥",
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            }
        }

        item {
            TextButton(
                onClick = onHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "查看完整學習紀錄",
                    color = HomeIconColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

private data class HomeFeature(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val action: () -> Unit
)

@Composable
private fun FeatureRow(
    a: HomeFeature,
    b: HomeFeature
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FeatureTile(
            feature = a,
            modifier = Modifier.weight(1f)
        )

        FeatureTile(
            feature = b,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FeatureTile(
    feature: HomeFeature,
    modifier: Modifier
) {
    Card(
        onClick = feature.action,
        modifier = modifier
            .heightIn(min = 132.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(22.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = feature.color
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                tint = HomeIconColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleSmall,
                color = HomeTitleColor,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = feature.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = HomeSubtitleColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    action: () -> Unit,
    modifier: Modifier
) {
    Card(
        onClick = action,
        modifier = modifier
            .heightIn(min = 92.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F4FA)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HomeIconColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = HomeTitleColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = HomeSecondaryColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

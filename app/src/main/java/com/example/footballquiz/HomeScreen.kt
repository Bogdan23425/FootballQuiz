package com.example.footballquiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartQuiz: () -> Unit,
    onLibrary: () -> Unit,
    onStats: () -> Unit,
    onSettings: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Football Quiz", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero / Intro card
            ElevatedCard(
                shape = shape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Прокачай знания о футболе",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Квиз с таймером, 3 жизни, очки и разные темы. Библиотека — чтобы быстро учить термины.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick actions grid (2x2)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCard(
                        title = "Квиз",
                        subtitle = "25 вопросов • таймер",
                        icon = Icons.Outlined.SportsSoccer,
                        onClick = onStartQuiz,
                        modifier = Modifier.weight(1f)
                    )
                    ActionCard(
                        title = "Библиотека",
                        subtitle = "термины и темы",
                        icon = Icons.Outlined.Book,
                        onClick = onLibrary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCard(
                        title = "Статистика",
                        subtitle = "прогресс и рекорды",
                        icon = Icons.Outlined.AutoGraph,
                        onClick = onStats,
                        modifier = Modifier.weight(1f)
                    )
                    ActionCard(
                        title = "Настройки",
                        subtitle = "звук и музыка",
                        icon = Icons.Outlined.Settings,
                        onClick = onSettings,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Small stats preview (пока заглушка — позже подключим реальную)
            ElevatedCard(shape = shape, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Сегодня",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    StatRow(label = "Лучший результат", value = "—")
                    StatRow(label = "Попыток", value = "—")
                    StatRow(label = "Серия побед", value = "—")
                }
            }
            ElevatedCard(shape = shape, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Тест звуков", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { AudioManager.playCorrect(context) },
                            modifier = Modifier.weight(1f)
                        ) { Text("Correct") }

                        Button(
                            onClick = { AudioManager.playWrong(context) },
                            modifier = Modifier.weight(1f)
                        ) { Text("Wrong") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)

    ElevatedCard(
        shape = shape,
        modifier = modifier.height(112.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

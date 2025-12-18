package com.example.footballquiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    val context = LocalContext.current
    var refreshKey by remember { mutableIntStateOf(0) }
    val maxScore = 250f

    val totalAttempts = remember(refreshKey) { StatsStore.attempts(context) }
    val bestTotal = remember(refreshKey) { StatsStore.bestScore(context) }
    val themes = remember { QuizData.themes + "Все темы" }

    Scaffold(
        topBar = { AppTopBar(title = "Статистика", canBack = true, onBack = onBack, onHome = onHome) }
    ) { padding ->
        AppBackground(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedCard(
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Общее", style = MaterialTheme.typography.titleLarge)
                        StatLine("Попыток", "$totalAttempts")
                        StatLine("Лучший результат", "$bestTotal")
                        LinearProgressIndicator(
                            progress = { (bestTotal / maxScore).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                ElevatedCard(
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("По темам", style = MaterialTheme.typography.titleLarge)
                        themes.forEach { t ->
                            val best = StatsStore.bestByTheme(context, t)
                            val value = if (best == 0) "—" else best.toString()
                            StatLine(t, value)
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                OutlinedButton(
                    onClick = {
                        StatsStore.reset(context)
                        refreshKey += 1
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сбросить статистику")
                }
            }
        }
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

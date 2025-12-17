package com.example.footballquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuizSetupScreen(
    onStart: (theme: String?) -> Unit
) {
    var selectedTheme by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Выбор квиза") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Выбери тему (или оставь все темы):",
                style = MaterialTheme.typography.titleMedium
            )

            FilterChip(
                selected = selectedTheme == null,
                onClick = { selectedTheme = null },
                label = { Text("Все темы") }
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuizData.themes.forEach { theme ->
                    FilterChip(
                        selected = selectedTheme == theme,
                        onClick = { selectedTheme = theme },
                        label = { Text(theme) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onStart(selectedTheme) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Начать квиз (25 вопросов)")
            }
        }
    }
}

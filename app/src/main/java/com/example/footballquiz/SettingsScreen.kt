package com.example.footballquiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    var musicEnabled by remember { mutableStateOf(AppPrefs.isMusicEnabled(context)) }
    var sfxEnabled by remember { mutableStateOf(AppPrefs.isSfxEnabled(context)) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Настройки") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingToggle(
                title = "Музыка",
                subtitle = "Фоновая музыка в приложении",
                checked = musicEnabled,
                onCheckedChange = { next ->
                    musicEnabled = next
                    AppPrefs.setMusicEnabled(context, next)
                    if (next) AudioManager.startMusic(context) else AudioManager.stopMusic()
                }
            )

            SettingToggle(
                title = "Звуки",
                subtitle = "Звук правильного/неправильного ответа",
                checked = sfxEnabled,
                onCheckedChange = { next ->
                    sfxEnabled = next
                    AppPrefs.setSfxEnabled(context, next)
                }
            )
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

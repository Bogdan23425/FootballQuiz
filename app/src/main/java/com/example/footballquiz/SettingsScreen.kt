package com.example.footballquiz

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    val context = LocalContext.current

    var musicEnabled by remember { mutableStateOf(AppPrefs.isMusicEnabled(context)) }
    var sfxEnabled by remember { mutableStateOf(AppPrefs.isSfxEnabled(context)) }
    var musicVol by remember { mutableFloatStateOf(AppPrefs.musicVolume(context)) }
    var sfxVol by remember { mutableFloatStateOf(AppPrefs.sfxVolume(context)) }

    fun applyVolumes() {
        AppPrefs.setMusicVolume(context, musicVol)
        AppPrefs.setSfxVolume(context, sfxVol)
        AudioManager.refreshVolumes(context)
    }

    Scaffold(
        topBar = { AppTopBar(title = "Настройки", canBack = true, onBack = onBack, onHome = onHome) }
    ) { padding ->
        AppBackground(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Аудио", style = MaterialTheme.typography.titleLarge)

                        ToggleRow(
                            title = "Музыка",
                            subtitle = "Фон в меню и в квизе",
                            checked = musicEnabled,
                            onCheckedChange = { next ->
                                musicEnabled = next
                                AppPrefs.setMusicEnabled(context, next)
                                if (next) {
                                    AudioManager.setMusicMode(context, MusicMode.MENU)
                                    AudioManager.refreshVolumes(context)
                                } else {
                                    AudioManager.stopMusic()
                                }
                            }
                        )

                        VolumeSlider(
                            title = "Громкость музыки",
                            value = musicVol,
                            enabled = musicEnabled,
                            onValueChange = {
                                musicVol = it
                                applyVolumes()
                            }
                        )

                        HorizontalDivider()

                        ToggleRow(
                            title = "Эффекты",
                            subtitle = "Ответы, таймер, победа/проигрыш",
                            checked = sfxEnabled,
                            onCheckedChange = { next ->
                                sfxEnabled = next
                                AppPrefs.setSfxEnabled(context, next)
                                AudioManager.refreshVolumes(context)
                            }
                        )

                        VolumeSlider(
                            title = "Громкость эффектов",
                            value = sfxVol,
                            enabled = sfxEnabled,
                            onValueChange = {
                                sfxVol = it
                                applyVolumes()
                            }
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Рекомендация",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Музыка 35–60%, эффекты 70–100% — так комфортно на большинстве устройств.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun VolumeSlider(
    title: String,
    value: Float,
    enabled: Boolean,
    onValueChange: (Float) -> Unit
) {
    val percent = (value * 100f).roundToInt().coerceIn(0, 100)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text("$percent%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            valueRange = 0f..1f
        )
    }
}

package com.example.footballquiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.max

private const val QUESTIONS_PER_QUIZ = 25
private const val SECONDS_PER_QUESTION = 15
private const val START_LIVES = 3
private const val SCORE_CORRECT = 10
private const val MAX_SCORE = QUESTIONS_PER_QUIZ * SCORE_CORRECT

private enum class GameOverReason { Lives, Timeout }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(
    theme: String?,
    onFinish: () -> Unit
) {
    val context = LocalContext.current

    val pool = remember(theme) {
        val filtered = if (theme.isNullOrBlank()) QuizData.all else QuizData.all.filter { it.theme == theme }
        val source = if (filtered.size >= QUESTIONS_PER_QUIZ) filtered else QuizData.all
        source.shuffled().take(QUESTIONS_PER_QUIZ)
    }

    var index by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(START_LIVES) }
    var score by remember { mutableIntStateOf(0) }

    var secondsLeft by remember { mutableIntStateOf(SECONDS_PER_QUESTION) }
    var timerKey by remember { mutableIntStateOf(0) }

    var locked by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }

    var isGameOver by remember { mutableStateOf(false) }
    var gameOverReason by remember { mutableStateOf(GameOverReason.Timeout) }

    val isFinished = index >= pool.size

    var warned3 by remember { mutableStateOf(false) }
    var warned2 by remember { mutableStateOf(false) }
    var warned1 by remember { mutableStateOf(false) }

    var didRecord by remember { mutableStateOf(false) }

    LaunchedEffect(timerKey, isGameOver, isFinished) {
        if (isGameOver || isFinished) return@LaunchedEffect

        secondsLeft = SECONDS_PER_QUESTION
        warned3 = false
        warned2 = false
        warned1 = false

        while (secondsLeft > 0 && !locked && !isGameOver) {
            delay(1_000)
            secondsLeft -= 1

            if (secondsLeft == 3 && !warned3) { warned3 = true; AudioManager.playTimeWarning(context) }
            if (secondsLeft == 2 && !warned2) { warned2 = true; AudioManager.playTimeWarning(context) }
            if (secondsLeft == 1 && !warned1) { warned1 = true; AudioManager.playTimeWarning(context) }
        }

        if (secondsLeft <= 0 && !locked && !isGameOver) {
            isGameOver = true
            gameOverReason = GameOverReason.Timeout
            AudioManager.playGameOver(context)
        }
    }

    LaunchedEffect(isGameOver, isFinished) {
        if (!didRecord && (isGameOver || isFinished)) {
            StatsStore.recordAttempt(context, theme, score)
            didRecord = true
        }
    }

    LaunchedEffect(isFinished) {
        if (isFinished) AudioManager.playWin(context)
    }

    BackHandler(enabled = true) { onFinish() }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Квиз", fontWeight = FontWeight.SemiBold) }) }
    ) { padding ->

        if (isGameOver) {
            QuizResultScreen(
                title = if (gameOverReason == GameOverReason.Timeout) "Время вышло" else "Game Over",
                subtitle = if (gameOverReason == GameOverReason.Timeout) "Ты не успел ответить вовремя." else "Ты потратил все жизни.",
                score = score,
                livesLeft = lives,
                onRestart = onFinish
            )
            return@Scaffold
        }

        if (isFinished) {
            QuizFinalByLivesScreen(
                score = score,
                livesLeft = lives,
                onRestart = onFinish
            )
            return@Scaffold
        }

        AppBackground(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Вопрос", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${index + 1} / ${pool.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("Жизни", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            LivesRow(lives = lives, totalLives = START_LIVES)
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                            Text("Очки", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                val progressTarget = secondsLeft / SECONDS_PER_QUESTION.toFloat()
                val progress by animateFloatAsState(targetValue = progressTarget, label = "timerProgress")
                val timerColor by animateColorAsState(
                    targetValue = if (secondsLeft <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    label = "timerColor"
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = timerColor
                )

                Text(
                    text = "Осталось: ${secondsLeft}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = timerColor
                )

                AnimatedContent(
                    targetState = index,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(180)) },
                    label = "questionSwap"
                ) { targetIndex ->
                    val q = pool[targetIndex]
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = q.theme,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = q.question,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        q.options.forEachIndexed { i, option ->
                            val isCorrectOption = i == q.correctIndex
                            val isSelected = i == selectedIndex

                            val targetContainer = when {
                                !locked -> MaterialTheme.colorScheme.surface
                                isSelected && isCorrectOption -> MaterialTheme.colorScheme.primaryContainer
                                isSelected && !isCorrectOption -> MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val targetContent = when {
                                !locked -> MaterialTheme.colorScheme.onSurface
                                isSelected && isCorrectOption -> MaterialTheme.colorScheme.onPrimaryContainer
                                isSelected && !isCorrectOption -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            val containerColor by animateColorAsState(targetValue = targetContainer, label = "optionBg")
                            val contentColor by animateColorAsState(targetValue = targetContent, label = "optionFg")

                            ElevatedButton(
                                onClick = {
                                    if (locked) return@ElevatedButton

                                    locked = true
                                    selectedIndex = i

                                    if (isCorrectOption) {
                                        score += SCORE_CORRECT
                                        AudioManager.playCorrect(context)
                                    } else {
                                        lives -= 1
                                        AudioManager.playWrong(context)
                                        AudioManager.playLifeLost(context)

                                        if (lives <= 0) {
                                            isGameOver = true
                                            gameOverReason = GameOverReason.Lives
                                            AudioManager.playGameOver(context)
                                            return@ElevatedButton
                                        }
                                    }
                                },
                                enabled = true,
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = containerColor,
                                    contentColor = contentColor,
                                    disabledContainerColor = containerColor,
                                    disabledContentColor = contentColor
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(option, modifier = Modifier.padding(vertical = 6.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        locked = false
                        selectedIndex = -1
                        index += 1
                        timerKey += 1
                    },
                    enabled = locked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Далее")
                }
            }
        }
    }
}

@Composable
private fun QuizFinalByLivesScreen(
    score: Int,
    livesLeft: Int,
    onRestart: () -> Unit
) {
    val (title, subtitle) = scoreTier(score)

    QuizResultScreen(
        title = title,
        subtitle = subtitle,
        score = score,
        livesLeft = livesLeft,
        onRestart = onRestart
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizResultScreen(
    title: String,
    subtitle: String,
    score: Int,
    livesLeft: Int,
    onRestart: () -> Unit
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Результат", fontWeight = FontWeight.SemiBold) }) }
    ) { padding ->
        AppBackground(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Очки: $score из $MAX_SCORE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Осталось жизней: $livesLeft",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Оценка по жизням: ${livesTierLabel(livesLeft)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Уровень по очкам: ${scoreTierLabel(score)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Верных ответов: ${score / SCORE_CORRECT} из $QUESTIONS_PER_QUIZ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
                    Text("Сыграть ещё раз")
                }
            }
        }
    }
}

@Composable
private fun LivesRow(lives: Int, totalLives: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(max(0, totalLives)) { index ->
            val isActive = index < lives
            Icon(
                imageVector = if (isActive) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun scoreTierLabel(score: Int): String {
    val ratio = score / MAX_SCORE.toFloat()
    return when {
        ratio >= 0.9f -> "Чемпион"
        ratio >= 0.6f -> "Крепкий результат"
        else -> "Нужно подтянуть знания"
    }
}

private fun scoreTier(score: Int): Pair<String, String> {
    val ratio = score / MAX_SCORE.toFloat()
    return when {
        ratio >= 0.9f -> "Ты чемпион 🏆" to "Сильный результат и отличная скорость."
        ratio >= 0.6f -> "Молодец, но ты можешь лучше 💪" to "Хороший результат, есть куда расти."
        else -> "Ты проиграл. Попробуй ещё 😅" to "Сейчас мало очков — сделай камбэк."
    }
}

private fun livesTierLabel(livesLeft: Int): String = when (livesLeft) {
    3 -> "Ты чемпион"
    1, 2 -> "Молодец, но ты можешь лучше"
    else -> "Ты проиграл. Попробуй ещё"
}

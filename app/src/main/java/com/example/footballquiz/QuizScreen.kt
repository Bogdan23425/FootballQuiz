package com.example.footballquiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
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

private enum class GameOverReason { Lives, Timeout }

@OptIn(ExperimentalMaterial3Api::class)
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

        val current = pool[index]

        Column(
            modifier = Modifier
                .padding(padding)
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
                    Column {
                        Text("Жизни", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("❤️".repeat(max(0, lives)), style = MaterialTheme.typography.titleMedium)
                    }
                    Column {
                        Text("Очки", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            LinearProgressIndicator(
                progress = { secondsLeft / SECONDS_PER_QUESTION.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Осталось: ${secondsLeft}s",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = current.theme,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = current.question,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            current.options.forEachIndexed { i, option ->
                val isCorrectOption = i == current.correctIndex
                val isSelected = i == selectedIndex

                val colors = when {
                    !locked -> ButtonDefaults.elevatedButtonColors()
                    locked && isCorrectOption -> ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    locked && isSelected && !isCorrectOption -> ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                    else -> ButtonDefaults.elevatedButtonColors()
                }

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
                    enabled = !locked,
                    colors = colors,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(option, modifier = Modifier.padding(vertical = 6.dp))
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

@Composable
private fun QuizFinalByLivesScreen(
    score: Int,
    livesLeft: Int,
    onRestart: () -> Unit
) {
    val (title, subtitle) = when (livesLeft) {
        3 -> "Ты чемпион 🏆" to "Прошёл квиз без ошибок — мощно!"
        1, 2 -> "Молодец, но ты можешь лучше 💪" to "Квиз пройден, но были ошибки."
        else -> "Ты проиграл. Попробуй ещё 😅" to "Жизни закончились — попробуй заново."
    }

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
        Column(
            modifier = Modifier
                .padding(padding)
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
                    Text("Очки: $score", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Осталось жизней: $livesLeft", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.weight(1f))

            Button(onClick = onRestart, modifier = Modifier.fillMaxWidth()) {
                Text("Сыграть ещё раз")
            }
        }
    }
}

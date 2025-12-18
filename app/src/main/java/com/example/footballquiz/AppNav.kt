package com.example.footballquiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

private sealed interface Screen {
    data object Home : Screen
    data object QuizSetup : Screen
    data class Quiz(val theme: String?) : Screen
    data object Library : Screen
    data object Stats : Screen
    data object Settings : Screen
}

private enum class NavMotion { Forward, Back }

@Composable
fun AppNav() {
    val context = LocalContext.current
    val stack = remember { mutableStateListOf<Screen>(Screen.Home) }
    var motion by remember { mutableStateOf(NavMotion.Forward) }

    val current = stack.last()
    val canPop = stack.size > 1

    fun navigate(next: Screen) {
        if (stack.last() == next) return
        motion = NavMotion.Forward
        stack.add(next)
    }

    fun pop() {
        if (stack.size <= 1) return
        motion = NavMotion.Back
        stack.removeAt(stack.lastIndex)
    }

    fun toHome() {
        if (stack.size == 1 && stack.last() is Screen.Home) return
        motion = NavMotion.Back
        stack.clear()
        stack.add(Screen.Home)
    }

    fun popToQuizSetup() {
        motion = NavMotion.Back
        val index = stack.indexOfLast { it is Screen.QuizSetup }
        if (index == -1) {
            stack.add(Screen.QuizSetup)
        } else {
            while (stack.size - 1 > index) {
                stack.removeAt(stack.lastIndex)
            }
        }
    }

    LaunchedEffect(current) {
        val mode = if (current is Screen.Quiz) MusicMode.QUIZ else MusicMode.MENU
        AudioManager.setMusicMode(context, mode)
    }

    val enterTransition = slideInHorizontally(initialOffsetX = { it / 3 }) + fadeIn()
    val exitTransition = slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut()
    val popEnterTransition = slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn()
    val popExitTransition = slideOutHorizontally(targetOffsetX = { it / 3 }) + fadeOut()

    BackHandler(enabled = canPop && current !is Screen.Quiz) {
        pop()
    }

    androidx.compose.animation.AnimatedContent(
        targetState = current,
        transitionSpec = {
            if (motion == NavMotion.Forward) {
                enterTransition togetherWith exitTransition
            } else {
                popEnterTransition togetherWith popExitTransition
            }
        },
        label = "navTransition"
    ) { screen ->
        when (screen) {
            Screen.Home -> {
                HomeScreen(
                    onStartQuiz = { navigate(Screen.QuizSetup) },
                    onLibrary = { navigate(Screen.Library) },
                    onStats = { navigate(Screen.Stats) },
                    onSettings = { navigate(Screen.Settings) },
                )
            }
            Screen.QuizSetup -> {
                QuizSetupScreen(
                    onBack = { pop() },
                    onHome = { toHome() },
                    onStart = { theme ->
                        val normalized = theme?.takeIf { it.isNotBlank() }
                        navigate(Screen.Quiz(normalized))
                    }
                )
            }
            is Screen.Quiz -> {
                QuizScreen(
                    theme = screen.theme,
                    onFinish = { popToQuizSetup() }
                )
            }
            Screen.Library -> {
                LibraryScreen(onBack = { pop() }, onHome = { toHome() })
            }
            Screen.Stats -> {
                StatsScreen(onBack = { pop() }, onHome = { toHome() })
            }
            Screen.Settings -> {
                SettingsScreen(onBack = { pop() }, onHome = { toHome() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    canBack: Boolean,
    onBack: (() -> Unit)?,
    onHome: (() -> Unit)?
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (onHome != null) {
                IconButton(onClick = onHome) {
                    Icon(Icons.Outlined.Home, contentDescription = "Home")
                }
            }
        }
    )
}

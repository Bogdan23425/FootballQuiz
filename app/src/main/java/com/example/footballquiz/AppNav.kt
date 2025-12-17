package com.example.footballquiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private object Routes {
    const val HOME = "home"
    const val QUIZ_SETUP = "quiz_setup"
    const val QUIZ = "quiz"
    const val LIBRARY = "library"
    const val STATS = "stats"
    const val SETTINGS = "settings"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onStartQuiz = { nav.navigate(Routes.QUIZ_SETUP) },
                onLibrary = { nav.navigate(Routes.LIBRARY) },
                onStats = { nav.navigate(Routes.STATS) },
                onSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }

        composable(Routes.QUIZ_SETUP) {
            QuizSetupScreen(
                onStart = { theme ->
                    val route = if (theme.isNullOrBlank()) {
                        "${Routes.QUIZ}?theme="
                    } else {
                        "${Routes.QUIZ}?theme=$theme"
                    }
                    nav.navigate(route)
                }
            )
        }

        composable(
            route = "${Routes.QUIZ}?theme={theme}",
            arguments = listOf(
                navArgument("theme") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val theme = backStackEntry.arguments?.getString("theme")

            QuizScreen(
                theme = theme,
                onFinish = {
                    // возвращаемся на экран выбора квиза
                    nav.popBackStack(Routes.QUIZ_SETUP, inclusive = false)
                }
            )
        }

        composable(Routes.LIBRARY) { PlaceholderScreen(title = "Библиотека") }
        composable(Routes.STATS) { PlaceholderScreen(title = "Статистика") }
        composable(Routes.SETTINGS) { SettingsScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(title) }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Скоро будет 👀", style = MaterialTheme.typography.titleMedium)
        }
    }
}

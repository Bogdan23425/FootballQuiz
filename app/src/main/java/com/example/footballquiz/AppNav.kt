package com.example.footballquiz

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
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
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route.orEmpty()

    LaunchedEffect(route) {
        val mode = if (route.startsWith(Routes.QUIZ)) MusicMode.QUIZ else MusicMode.MENU
        AudioManager.setMusicMode(nav.context, mode)
    }

    fun toHome() {
        nav.navigate(Routes.HOME) {
            popUpTo(Routes.HOME) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(navController = nav, startDestination = Routes.HOME) {

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
                    val t = theme.orEmpty()
                    nav.navigate("${Routes.QUIZ}?theme=$t")
                }
            )
        }

        composable(
            route = "${Routes.QUIZ}?theme={theme}",
            arguments = listOf(navArgument("theme") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val theme = backStackEntry.arguments?.getString("theme")?.takeIf { it.isNotBlank() }
            QuizScreen(
                theme = theme,
                onFinish = { nav.popBackStack(Routes.QUIZ_SETUP, inclusive = false) }
            )
        }

        composable(Routes.LIBRARY) {
            LibraryScreen(onBack = { nav.popBackStack() }, onHome = { toHome() })
        }

        composable(Routes.STATS) {
            StatsScreen(onBack = { nav.popBackStack() }, onHome = { toHome() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { nav.popBackStack() }, onHome = { toHome() })
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

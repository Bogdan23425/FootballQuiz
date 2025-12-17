package com.example.footballquiz

import android.content.Context

object StatsStore {
    private const val FILE = "football_quiz_stats"
    private const val KEY_ATTEMPTS = "attempts_total"
    private const val KEY_BEST = "best_total"

    private fun sp(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun attempts(context: Context): Int = sp(context).getInt(KEY_ATTEMPTS, 0)
    fun bestScore(context: Context): Int = sp(context).getInt(KEY_BEST, 0)

    fun bestByTheme(context: Context, theme: String): Int =
        sp(context).getInt("best_theme_$theme", 0)

    fun recordAttempt(context: Context, theme: String?, score: Int) {
        val t = theme?.takeIf { it.isNotBlank() } ?: "Все темы"
        val s = sp(context)

        val nextAttempts = s.getInt(KEY_ATTEMPTS, 0) + 1
        val best = maxOf(s.getInt(KEY_BEST, 0), score)
        val bestTheme = maxOf(s.getInt("best_theme_$t", 0), score)

        s.edit()
            .putInt(KEY_ATTEMPTS, nextAttempts)
            .putInt(KEY_BEST, best)
            .putInt("best_theme_$t", bestTheme)
            .apply()
    }

    fun reset(context: Context) {
        sp(context).edit().clear().apply()
    }
}

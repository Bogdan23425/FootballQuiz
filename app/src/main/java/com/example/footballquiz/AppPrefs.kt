package com.example.footballquiz

import android.content.Context

object AppPrefs {
    private const val FILE = "football_quiz_prefs"
    private const val KEY_MUSIC = "music_enabled"
    private const val KEY_SFX = "sfx_enabled"

    fun isMusicEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getBoolean(KEY_MUSIC, true)

    fun setMusicEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_MUSIC, enabled)
            .apply()
    }

    fun isSfxEnabled(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getBoolean(KEY_SFX, true)

    fun setSfxEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SFX, enabled)
            .apply()
    }
}

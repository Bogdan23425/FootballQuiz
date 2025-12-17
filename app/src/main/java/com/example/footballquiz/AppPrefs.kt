package com.example.footballquiz

import android.content.Context

object AppPrefs {
    private const val FILE = "football_quiz_prefs"

    private const val KEY_MUSIC = "music_enabled"
    private const val KEY_SFX = "sfx_enabled"
    private const val KEY_MUSIC_VOL = "music_volume"
    private const val KEY_SFX_VOL = "sfx_volume"

    private fun sp(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun isMusicEnabled(context: Context): Boolean =
        sp(context).getBoolean(KEY_MUSIC, true)

    fun setMusicEnabled(context: Context, enabled: Boolean) {
        sp(context).edit().putBoolean(KEY_MUSIC, enabled).apply()
    }

    fun isSfxEnabled(context: Context): Boolean =
        sp(context).getBoolean(KEY_SFX, true)

    fun setSfxEnabled(context: Context, enabled: Boolean) {
        sp(context).edit().putBoolean(KEY_SFX, enabled).apply()
    }

    fun musicVolume(context: Context): Float =
        sp(context).getFloat(KEY_MUSIC_VOL, 0.55f).coerceIn(0f, 1f)

    fun setMusicVolume(context: Context, volume: Float) {
        sp(context).edit().putFloat(KEY_MUSIC_VOL, volume.coerceIn(0f, 1f)).apply()
    }

    fun sfxVolume(context: Context): Float =
        sp(context).getFloat(KEY_SFX_VOL, 0.9f).coerceIn(0f, 1f)

    fun setSfxVolume(context: Context, volume: Float) {
        sp(context).edit().putFloat(KEY_SFX_VOL, volume.coerceIn(0f, 1f)).apply()
    }
}

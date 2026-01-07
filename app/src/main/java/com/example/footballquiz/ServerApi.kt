package com.example.footballquiz

import android.content.Context
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

object ServerApi {

    private const val BASE_URL = "https://metricplay.click/api/app/"
    private const val TAG = "SERVER_API"

    suspend fun fetchUrl(context: Context): String? {
        val bundle = context.packageName
        val country = DeviceInfo.simCountryGeo(context)

        if (country.isNullOrEmpty()) {
            Log.e(TAG, "Country is null → open game")
            return null
        }

        // 🔥 КЛЮЧЕВОЕ МЕСТО
        val fullUrl = BASE_URL + bundle

        Log.d(TAG, "REQUEST -> $fullUrl")
        Log.d(TAG, "PARAMS -> country=$country")

        return try {
            val url = URL(fullUrl)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
            )
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.doOutput = true

            // ❗ ТОЛЬКО country
            val body = "country=$country"
            connection.outputStream.use {
                it.write(body.toByteArray())
            }

            val code = connection.responseCode
            Log.d(TAG, "RESPONSE CODE -> $code")

            if (code == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                Log.d(TAG, "RESPONSE BODY -> $response")

                response.takeIf { it.isNotBlank() }
            } else {
                Log.e(TAG, "SERVER ERROR -> $code")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "REQUEST FAILED", e)
            null
        }
    }
}

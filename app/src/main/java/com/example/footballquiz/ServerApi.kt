package com.example.footballquiz

import android.content.Context
import java.net.HttpURLConnection
import java.net.URL

object ServerApi {
    private const val API_URL = "https://metricplay.click/api/app/"

    suspend fun fetchUrl(context: Context): String? {
        val bundle = context.packageName
        val country = DeviceInfo.simCountryGeo(context)

        val params = buildString {
            append("bundle=").append(bundle)
            country?.let { append("&country=").append(it) }
        }

        return try {
            val url = URL(API_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doOutput = true
            connection.outputStream.use { it.write(params.toByteArray()) }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                response.takeIf { it.isNotBlank() }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}


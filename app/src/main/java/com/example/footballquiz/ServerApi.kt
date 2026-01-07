package com.example.footballquiz

import android.content.Context
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

object ServerApi {
    private const val API_URL = "https://metricplay.click/api/app/"
    private const val TAG = "SERVER_API"

    suspend fun fetchUrl(context: Context): String? {
        val bundle = context.packageName
        val country = DeviceInfo.simCountryGeo(context)

        val params = buildString {
            append("bundle=").append(bundle)
            country?.let { append("&country=").append(it) }
        }

        Log.d(TAG, "REQUEST -> $API_URL")
        Log.d(TAG, "PARAMS -> $params")

        return try {
            val url = URL(API_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
            )
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.doOutput = true

            connection.outputStream.use {
                it.write(params.toByteArray())
            }

            val responseCode = connection.responseCode
            Log.d(TAG, "RESPONSE CODE -> $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                Log.d(TAG, "RESPONSE BODY -> $response")

                response.takeIf { it.isNotBlank() }
            } else {
                Log.e(TAG, "SERVER ERROR CODE -> $responseCode")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "REQUEST FAILED", e)
            null
        }
    }
}

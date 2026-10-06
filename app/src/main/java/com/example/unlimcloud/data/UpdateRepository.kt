package com.example.unlimcloud.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class UpdateRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    sealed class UpdateResult {
        data class HasUpdate(val currentVersion: String, val latestVersion: String) : UpdateResult()
        data class UpToDate(val version: String) : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }

    suspend fun checkVersion(currentVersion: String = "2.0.0"): UpdateResult = withContext(Dispatchers.IO) {
        val url = "https://raw.githubusercontent.com/inulute/unlim-cloud/main/package.json"
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext UpdateResult.Error("Network error: HTTP ${response.code}")
                }
                val body = response.body?.string() ?: return@withContext UpdateResult.Error("Empty response")
                val pkg = json.decodeFromString<PackageInfo>(body)
                val comparison = compareVersions(pkg.version, currentVersion)
                if (comparison > 0) {
                    UpdateResult.HasUpdate(currentVersion = currentVersion, latestVersion = pkg.version)
                } else {
                    UpdateResult.UpToDate(version = pkg.version)
                }
            }
        } catch (e: Exception) {
            UpdateResult.Error(e.message ?: "Failed to verify updates. Check your connection.")
        }
    }

    companion object {
        fun compareVersions(v1: String, v2: String): Int {
            val p1 = v1.trim().removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
            val p2 = v2.trim().removePrefix("v").split(".").mapNotNull { it.toIntOrNull() }
            val maxLen = maxOf(p1.size, p2.size)
            for (i in 0 until maxLen) {
                val num1 = p1.getOrElse(i) { 0 }
                val num2 = p2.getOrElse(i) { 0 }
                if (num1 > num2) return 1
                if (num1 < num2) return -1
            }
            return 0
        }
    }
}

package com.example.unlimcloud.data

import kotlinx.serialization.Serializable

@Serializable
data class PackageInfo(
    val name: String = "",
    val version: String = "2.0.0"
)

enum class StorageCategory {
    ALL,
    DOCUMENTS,
    MEDIA,
    ARCHIVES,
    OTHER
}

enum class SortOption(val label: String) {
    NAME_ASC("Name (A to Z)"),
    NAME_DESC("Name (Z to A)"),
    DATE_DESC("Date Modified (Newest)"),
    DATE_ASC("Date Modified (Oldest)"),
    SIZE_DESC("Size (Largest)"),
    SIZE_ASC("Size (Smallest)")
}

data class CloudFile(
    val id: String,
    val name: String,
    val sizeBytes: Long,
    val category: StorageCategory,
    val modifiedTime: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isFolder: Boolean = false,
    val folderPath: String = "/",
    val telegramMessageId: Long? = null,
    val previewUrl: String? = null
) {
    val formattedSize: String
        get() {
            if (isFolder) return "--"
            val kb = sizeBytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format("%.2f GB", gb)
                mb >= 1.0 -> String.format("%.1f MB", mb)
                kb >= 1.0 -> String.format("%.0f KB", kb)
                else -> "$sizeBytes B"
            }
        }
}

data class TelegramSession(
    val telegramId: String,
    val username: String,
    val displayName: String,
    val totalFiles: Int,
    val totalStorageUsedBytes: Long,
    val totalQuotaBytes: Long = 10L * 1024 * 1024 * 1024, // 10 GB visual quota allocation or unlimited
    val isLoggedIn: Boolean
)

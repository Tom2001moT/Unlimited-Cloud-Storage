package com.example.unlimcloud.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.unlimcloud.data.CloudFile
import com.example.unlimcloud.data.StorageCategory

@Entity(tableName = "cached_files")
data class CachedFileEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val sizeBytes: Long,
    val categoryName: String,
    val modifiedTime: String,
    val timestampMillis: Long,
    val isFolder: Boolean,
    val folderPath: String,
    val telegramMessageId: Long?,
    val previewUrl: String?
) {
    fun toCloudFile(): CloudFile {
        val cat = try {
            StorageCategory.valueOf(categoryName)
        } catch (e: Exception) {
            StorageCategory.OTHER
        }
        return CloudFile(
            id = id,
            name = name,
            sizeBytes = sizeBytes,
            category = cat,
            modifiedTime = modifiedTime,
            timestampMillis = timestampMillis,
            isFolder = isFolder,
            folderPath = folderPath,
            telegramMessageId = telegramMessageId,
            previewUrl = previewUrl
        )
    }

    companion object {
        fun fromCloudFile(file: CloudFile): CachedFileEntity {
            return CachedFileEntity(
                id = file.id,
                name = file.name,
                sizeBytes = file.sizeBytes,
                categoryName = file.category.name,
                modifiedTime = file.modifiedTime,
                timestampMillis = file.timestampMillis,
                isFolder = file.isFolder,
                folderPath = file.folderPath,
                telegramMessageId = file.telegramMessageId,
                previewUrl = file.previewUrl
            )
        }
    }
}

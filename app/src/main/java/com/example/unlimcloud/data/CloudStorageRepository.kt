package com.example.unlimcloud.data

import com.example.unlimcloud.data.local.CachedFileDao
import com.example.unlimcloud.data.local.CachedFileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CloudStorageRepository(
    private val cachedFileDao: CachedFileDao? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val _session = MutableStateFlow<TelegramSession?>(null)
    val session: StateFlow<TelegramSession?> = _session.asStateFlow()

    private val _files = MutableStateFlow<List<CloudFile>>(emptyList())
    val files: StateFlow<List<CloudFile>> = _files.asStateFlow()

    init {
        scope.launch {
            // Load real persisted files from Room database
            val cached = cachedFileDao?.getAllCachedFiles()
            if (!cached.isNullOrEmpty()) {
                val mapped = cached.map { it.toCloudFile() }
                _files.value = mapped
                recalculateSession("Connected", "telegram_user", "Telegram Cloud Storage", mapped)
            } else {
                _files.value = emptyList()
                _session.value = null
            }
        }
    }

    fun login(telegramId: String, username: String, displayName: String = "Telegram Account") {
        scope.launch {
            val existing = cachedFileDao?.getAllCachedFiles() ?: emptyList()
            val mapped = existing.map { it.toCloudFile() }
            _files.value = mapped
            recalculateSession(telegramId, username, displayName, mapped)
        }
    }

    fun logout() {
        _session.value = null
        _files.value = emptyList()
        scope.launch {
            cachedFileDao?.clearAll()
        }
    }

    fun addFile(name: String, sizeBytes: Long, category: StorageCategory, isFolder: Boolean = false, previewUrl: String? = null) {
        val newFile = CloudFile(
            id = UUID.randomUUID().toString(),
            name = name,
            sizeBytes = sizeBytes,
            category = category,
            modifiedTime = "Just now",
            timestampMillis = System.currentTimeMillis(),
            isFolder = isFolder,
            telegramMessageId = null,
            previewUrl = previewUrl
        )
        val updated = listOf(newFile) + _files.value
        _files.value = updated
        _session.value?.let { current ->
            recalculateSession(current.telegramId, current.username, current.displayName, updated)
        }

        // Cache update asynchronously in Room database
        scope.launch {
            cachedFileDao?.insertOrUpdate(CachedFileEntity.fromCloudFile(newFile))
        }
    }

    fun deleteFile(id: String) {
        val updated = _files.value.filterNot { it.id == id }
        _files.value = updated
        _session.value?.let { current ->
            recalculateSession(current.telegramId, current.username, current.displayName, updated)
        }

        // Remove from Room local cache
        scope.launch {
            cachedFileDao?.deleteById(id)
        }
    }

    fun deleteFiles(ids: Set<String>) {
        val updated = _files.value.filterNot { ids.contains(it.id) }
        _files.value = updated
        _session.value?.let { current ->
            recalculateSession(current.telegramId, current.username, current.displayName, updated)
        }

        // Batch delete from Room local cache
        scope.launch {
            cachedFileDao?.deleteByIds(ids.toList())
        }
    }

    fun moveFiles(ids: Set<String>, targetFolderPath: String) {
        val updated = _files.value.map { file ->
            if (ids.contains(file.id)) {
                file.copy(folderPath = targetFolderPath)
            } else {
                file
            }
        }
        _files.value = updated

        // Batch update folder in Room database
        scope.launch {
            cachedFileDao?.updateFolderPath(ids.toList(), targetFolderPath)
        }
    }

    private fun recalculateSession(telegramId: String, username: String, displayName: String, list: List<CloudFile>) {
        val totalBytes = list.sumOf { it.sizeBytes }
        _session.value = TelegramSession(
            telegramId = telegramId,
            username = username,
            displayName = displayName,
            totalFiles = list.size,
            totalStorageUsedBytes = totalBytes,
            isLoggedIn = true
        )
    }
}

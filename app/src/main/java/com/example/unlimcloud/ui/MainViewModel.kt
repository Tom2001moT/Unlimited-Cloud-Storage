package com.example.unlimcloud.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unlimcloud.data.CloudFile
import com.example.unlimcloud.data.CloudStorageRepository
import com.example.unlimcloud.data.SortOption
import com.example.unlimcloud.data.StorageCategory
import com.example.unlimcloud.data.TelegramSession
import com.example.unlimcloud.data.UpdateRepository
import com.example.unlimcloud.data.local.UnlimDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Splash : ScreenDestination()
    object Explorer : ScreenDestination()
    object Gallery : ScreenDestination()
    object Updates : ScreenDestination()
    object Donate : ScreenDestination()
    object WebPortal : ScreenDestination()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val updateRepo: UpdateRepository = UpdateRepository()
    private val storageRepo: CloudStorageRepository = CloudStorageRepository(
        cachedFileDao = UnlimDatabase.getInstance(application).cachedFileDao()
    )

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Splash)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateRepository.UpdateResult?>(null)
    val updateState: StateFlow<UpdateRepository.UpdateResult?> = _updateState.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(StorageCategory.ALL)
    val selectedCategory: StateFlow<StorageCategory> = _selectedCategory.asStateFlow()

    private val _selectedSortOption = MutableStateFlow(SortOption.DATE_DESC)
    val selectedSortOption: StateFlow<SortOption> = _selectedSortOption.asStateFlow()

    private val _isGridViewMode = MutableStateFlow(false)
    val isGridViewMode: StateFlow<Boolean> = _isGridViewMode.asStateFlow()

    val session: StateFlow<TelegramSession?> = storageRepo.session

    val filteredFiles: StateFlow<List<CloudFile>> = combine(
        storageRepo.files,
        _searchQuery,
        _selectedCategory,
        _selectedSortOption
    ) { files, query, cat, sort ->
        val filtered = files.filter { file ->
            val matchesCategory = (cat == StorageCategory.ALL) || (file.category == cat)
            val matchesQuery = query.isBlank() || file.name.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        when (sort) {
            SortOption.NAME_ASC -> filtered.sortedWith(compareBy<CloudFile> { !it.isFolder }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })
            SortOption.NAME_DESC -> filtered.sortedWith(compareBy<CloudFile> { !it.isFolder }.thenByDescending(String.CASE_INSENSITIVE_ORDER) { it.name })
            SortOption.DATE_DESC -> filtered.sortedWith(compareBy<CloudFile> { !it.isFolder }.thenByDescending { it.timestampMillis })
            SortOption.DATE_ASC -> filtered.sortedWith(compareBy<CloudFile> { !it.isFolder }.thenBy { it.timestampMillis })
            SortOption.SIZE_DESC -> filtered.sortedWith(compareBy<CloudFile> { !it.isFolder }.thenByDescending { it.sizeBytes })
            SortOption.SIZE_ASC -> filtered.sortedWith(compareBy<CloudFile> { !it.isFolder }.thenBy { it.sizeBytes })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mediaFiles: StateFlow<List<CloudFile>> = combine(storageRepo.files, _searchQuery) { files, query ->
        files.filter { it.category == StorageCategory.MEDIA && (query.isBlank() || it.name.contains(query, ignoreCase = true)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        checkForUpdates(silentCheck = true)
    }

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: StorageCategory) {
        _selectedCategory.value = category
    }

    fun setSortOption(sortOption: SortOption) {
        _selectedSortOption.value = sortOption
    }

    fun toggleGridViewMode() {
        _isGridViewMode.value = !_isGridViewMode.value
    }

    fun setGridViewMode(grid: Boolean) {
        _isGridViewMode.value = grid
    }

    fun checkForUpdates(silentCheck: Boolean = false) {
        viewModelScope.launch {
            _isCheckingUpdate.value = true
            val result = updateRepo.checkVersion(currentVersion = "2.0.0")
            _updateState.value = result
            _isCheckingUpdate.value = false

            if (silentCheck && _currentScreen.value is ScreenDestination.Splash) {
                when (result) {
                    is UpdateRepository.UpdateResult.HasUpdate -> {
                        // Stay on splash or route to updates notice
                    }
                    else -> {
                        // If no update or error, automatically transition after a pleasant intro splash
                        kotlinx.coroutines.delay(1200)
                        if (_currentScreen.value is ScreenDestination.Splash) {
                            _currentScreen.value = ScreenDestination.Explorer
                        }
                    }
                }
            }
        }
    }

    fun uploadSampleFile(name: String, category: StorageCategory, sizeBytes: Long) {
        storageRepo.addFile(name, sizeBytes, category)
    }

    fun deleteFile(id: String) {
        storageRepo.deleteFile(id)
    }

    fun deleteFiles(ids: Set<String>) {
        storageRepo.deleteFiles(ids)
    }

    fun moveFiles(ids: Set<String>, destinationFolder: String) {
        storageRepo.moveFiles(ids, destinationFolder)
    }

    fun login(telegramId: String, username: String) {
        storageRepo.login(telegramId, username, "Telegram Account")
    }

    fun logout() {
        storageRepo.logout()
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        return MainViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}

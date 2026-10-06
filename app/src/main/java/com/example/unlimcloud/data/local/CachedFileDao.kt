package com.example.unlimcloud.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedFileDao {

    @Query("SELECT * FROM cached_files ORDER BY timestampMillis DESC")
    fun getAllCachedFilesFlow(): Flow<List<CachedFileEntity>>

    @Query("SELECT * FROM cached_files")
    suspend fun getAllCachedFiles(): List<CachedFileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(file: CachedFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(files: List<CachedFileEntity>)

    @Query("DELETE FROM cached_files WHERE id = :fileId")
    suspend fun deleteById(fileId: String)

    @Query("DELETE FROM cached_files WHERE id IN (:fileIds)")
    suspend fun deleteByIds(fileIds: List<String>)

    @Query("UPDATE cached_files SET folderPath = :targetFolder WHERE id IN (:fileIds)")
    suspend fun updateFolderPath(fileIds: List<String>, targetFolder: String)

    @Query("DELETE FROM cached_files")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM cached_files")
    suspend fun getCount(): Int
}

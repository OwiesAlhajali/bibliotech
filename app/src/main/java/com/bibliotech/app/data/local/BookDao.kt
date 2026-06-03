package com.bibliotech.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bibliotech.app.data.model.BookEntity
import com.bibliotech.app.data.model.RecentBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {


    @Query("SELECT * FROM BookEntity")
    fun getAllCachedBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(book: BookEntity)

    @Delete
    suspend fun deleteFavorite(book: BookEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM BookEntity WHERE `key` = :bookKey)")
    suspend fun isBookFavorite(bookKey: String): Boolean



    @Query("SELECT * FROM recent_books ORDER BY timestamp DESC")
    fun getRecentBooks(): Flow<List<RecentBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(book: RecentBookEntity)

    @Query("DELETE FROM recent_books WHERE `key` = :bookKey")
    suspend fun deleteRecentByKey(bookKey: String)

    @Query("SELECT COUNT(*) FROM recent_books")
    suspend fun getRecentCount(): Int

    @Query("DELETE FROM recent_books WHERE timestamp = (SELECT MIN(timestamp) FROM recent_books)")
    suspend fun deleteOldestRecent()
}
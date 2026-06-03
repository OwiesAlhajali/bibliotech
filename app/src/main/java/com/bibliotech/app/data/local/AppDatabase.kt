package com.bibliotech.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bibliotech.app.data.model.BookEntity
import com.bibliotech.app.data.model.RecentBookEntity

@Database(entities = [BookEntity::class, RecentBookEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bibliotech_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
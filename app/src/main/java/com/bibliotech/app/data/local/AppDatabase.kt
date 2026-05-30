package com.bibliotech.app.data.local // تأكد من اسم الباكيدج عندك

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bibliotech.app.data.model.BookEntity
import com.bibliotech.app.data.model.RecentBookEntity

// 1. تسجيل الجداول والإصدار
@Database(entities = [BookEntity::class, RecentBookEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    // ... باقي كود الـ Companion object


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 3. دالة بناء قاعدة البيانات بنمط Singleton (عشان ما يتكرر إنشاؤها بالذاكرة)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bibliotech_database" // اسم ملف قاعدة البيانات على الجهاز
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
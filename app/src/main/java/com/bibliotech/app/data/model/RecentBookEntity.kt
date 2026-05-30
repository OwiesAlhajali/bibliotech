package com.bibliotech.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_books")
data class RecentBookEntity(
    @PrimaryKey val key: String,
    val title: String,
    val cover_i: Int?,
    val author_name: String?,
    val timestamp: Long // هاد الحقل السحري عشان نرتب بيه الأحدث فالأحدث تلقائياً!
)
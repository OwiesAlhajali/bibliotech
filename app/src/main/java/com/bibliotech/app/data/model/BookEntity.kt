package com.bibliotech.app.data.model // تأكد من اسم الباكيدج عندك

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. تحديد اسم الجدول في قاعدة البيانات
@Entity(tableName = "BookEntity")
data class BookEntity(
    // 2. المفتاح الأساسي وهو الـ key الفريد لكل كتاب
    @PrimaryKey
    val key: String,
    val title: String,
    val cover_i: Int?,
    val author_name: String?
)
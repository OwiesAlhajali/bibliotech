package com.bibliotech.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BookEntity")
data class BookEntity(

    @PrimaryKey
    val key: String,
    val title: String,
    val cover_i: Int?,
    val author_name: String?
)
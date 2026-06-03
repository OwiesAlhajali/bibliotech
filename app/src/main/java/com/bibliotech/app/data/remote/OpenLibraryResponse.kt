package com.bibliotech.app.data.remote

data class OpenLibraryResponse(
    val docs: List<BookDoc>
)

data class BookDoc(
    val key: String,
    val title: String,
    val author_name: List<String>?,
    val cover_i: Int?,
)
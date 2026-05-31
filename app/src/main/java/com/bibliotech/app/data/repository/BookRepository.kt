package com.bibliotech.app.data.repository

import com.bibliotech.app.data.local.BookDao
import com.bibliotech.app.data.model.BookDetailsResponse
import com.bibliotech.app.data.model.BookEntity
import com.bibliotech.app.data.model.RecentBookEntity
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.data.remote.OpenLibraryApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(
    private val apiService: OpenLibraryApiService,
    private val bookDao: BookDao
) {

    private val defaultBooks = listOf(
        BookDoc("/works/OL258925W", "Rich Dad Poor Dad", listOf("Robert T. Kiyosaki"), 12644265),
        BookDoc("/works/OL82563W", "Harry Potter and the Philosopher's Stone", listOf("J. K. Rowling"), 10521236),
        BookDoc("/works/OL27349W", "The Alchemist", listOf("Paulo Coelho"), 8251263),
        BookDoc("/works/OL1965611W", "Think and Grow Rich", listOf("Napoleon Hill"), 9254123),
        BookDoc("/works/OL262529W", "The 5 AM Club", listOf("Robin Sharma"), 1124512),
        BookDoc("/works/OL45322W", "Atomic Habits", listOf("James Clear"), 1236451)
    )

    suspend fun searchBooks(query: String): List<BookDoc> {
        return try {
            val response = apiService.searchBooks(query)
            response.docs
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ==================== [ قسم المفضلة - REALTIME ROOM ] ====================

    fun getFavoriteBooks(): Flow<List<BookDoc>> {
        return bookDao.getAllCachedBooks().map { entities ->
            entities.map { entity ->
                BookDoc(
                    key = entity.key,
                    title = entity.title,
                    author_name = entity.author_name?.let { listOf(it) },
                    cover_i = entity.cover_i
                )
            }
        }
    }

    suspend fun toggleFavoriteBook(book: BookDoc) {
        val entity = BookEntity(
            key = book.key,
            title = book.title,
            cover_i = book.cover_i,
            author_name = book.author_name?.firstOrNull()
        )
        if (bookDao.isBookFavorite(book.key)) {
            bookDao.deleteFavorite(entity)
        } else {
            bookDao.insertFavorite(entity)
        }
    }

    // ==================== [ قسم الكتب الأخيرة - RECENT SEARCHES ] ====================

    fun getRecentBooks(): Flow<List<BookDoc>> {
        return bookDao.getRecentBooks().map { entities ->
            if (entities.isEmpty()) {
                defaultBooks
            } else {
                entities.map { entity ->
                    BookDoc(
                        key = entity.key,
                        title = entity.title,
                        author_name = entity.author_name?.let { listOf(it) },
                        cover_i = entity.cover_i
                    )
                }
            }
        }
    }

    suspend fun saveBookToCache(book: BookDoc) {
        bookDao.deleteRecentByKey(book.key)

        val newRecent = RecentBookEntity(
            key = book.key,
            title = book.title,
            cover_i = book.cover_i,
            author_name = book.author_name?.firstOrNull(),
            timestamp = System.currentTimeMillis()
        )
        bookDao.insertRecent(newRecent)

        if (bookDao.getRecentCount() > 6) {
            bookDao.deleteOldestRecent()
        }
    }
    suspend fun getBookDetails(bookKey: String): BookDetailsResponse {
        return apiService.getBookDetails(bookKey)
    }
}
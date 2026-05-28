package com.bibliotech.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.data.remote.OpenLibraryApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookRepository(
    private val apiService: OpenLibraryApiService,
    private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("book_cache_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val cacheKey = "recent_books"

    // القائمة الافتراضية العالمية للكتب الـ 6
    private val defaultBooks = listOf(
        BookDoc("/works/OL258925W", "Rich Dad Poor Dad", listOf("Robert T. Kiyosaki"), 12644265),
        BookDoc("/works/OL82563W", "Harry Potter and the Philosopher's Stone", listOf("J. K. Rowling"), 10521236),
        BookDoc("/works/OL27349W", "The Alchemist", listOf("Paulo Coelho"), 8251263),
        BookDoc("/works/OL1965611W", "Think and Grow Rich", listOf("Napoleon Hill"), 9254123),
        BookDoc("/works/OL262529W", "The 5 AM Club", listOf("Robin Sharma"), 1124512),
        BookDoc("/works/OL45322W", "Atomic Habits", listOf("James Clear"), 1236451)
    )

    // 1. جلب كتب البحث من السيرفر
    suspend fun searchBooks(query: String): List<BookDoc> {
        return try {
            val response = apiService.searchBooks(query)
            response.docs
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 2. قراءة الكاش (إذا كان فارغاً، يرجع الـ 6 كتب الافتراضية فوراً)
    fun getRecentBooks(): List<BookDoc> {
        val json = sharedPreferences.getString(cacheKey, null) ?: return defaultBooks
        val type = object : TypeToken<List<BookDoc>>() {}.type
        return gson.fromJson(json, type)
    }

    // 3. حفظ الكتاب مع دمج القائمة وتطبيق سياسة الإزاحة بالتتالي (FIFO)
    fun saveBookToCache(newBook: BookDoc) {
        // نجلب القائمة الحالية (سواء كانت الكاش أو الافتراضية إذا كان الكاش فارغاً)
        val currentList = getRecentBooks().toMutableList()

        // إذا كان الكتاب المبحوث عنه موجوداً مسبقاً في القائمة، نحذفه لتجنب التكرار ولنعيده للمقدمة
        currentList.removeAll { it.key == newBook.key }

        // إضافة الكتاب الجديد في أول خيار (المرتبة 0)
        currentList.add(0, newBook)

        // إذا تجاوز إجمالي الكتب 6، نحذف الخيار الأخير بالتتالي (سواء كان افتراضياً أو قديماً)
        if (currentList.size > 6) {
            currentList.removeAt(currentList.size - 1)
        }

        // حفظ القائمة الجديدة المدمجة بالـ SharedPreferences
        val json = gson.toJson(currentList)
        sharedPreferences.edit().putString(cacheKey, json).apply()
    }
}
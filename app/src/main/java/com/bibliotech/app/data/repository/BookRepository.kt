package com.bibliotech.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.data.remote.OpenLibraryApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
// الـ Imports الثلاثة السحرية يلي كانت ناقصة وطيرت الـ Flow والـ emit:
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

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

    // 2. قراءة الكاش كـ Flow بخلفية مريحة لحماية الإقلاع
    fun getRecentBooks(): Flow<List<BookDoc>> = flow {
        val jsonString = sharedPreferences.getString(cacheKey, null)
        if (jsonString == null) {
            emit(defaultBooks)
        } else {
            val type = object : TypeToken<List<BookDoc>>() {}.type
            val booksList: List<BookDoc> = gson.fromJson(jsonString, type)
            emit(booksList)
        }
    }.flowOn(Dispatchers.IO) // ضفنا هاد السطر لضمان عدم حصول أي ثقل بالإقلاع

    // 3. حفظ الكتاب مع دمج القائمة وتطبيق سياسة الإزاحة بالتتالي (FIFO)
    fun saveBookToCache(newBook: BookDoc) {
        val jsonString = sharedPreferences.getString(cacheKey, null)
        val currentList = if (jsonString != null) {
            val type = object : TypeToken<List<BookDoc>>() {}.type
            gson.fromJson<List<BookDoc>>(jsonString, type).toMutableList()
        } else {
            defaultBooks.toMutableList()
        }

        // إذا الكتاب مكرر بنحذفه القديم عشان ينزل بالمرتبة 0 كأحدث كتاب
        if (currentList.contains(newBook)) {
            currentList.remove(newBook)
        }

        // إضافة الكتاب الجديد في أول خيار (المرتبة 0)
        currentList.add(0, newBook)

        // إذا تجاوز إجمالي الكتب 6، نحذف الخيار الأخير بالتتالي
        if (currentList.size > 6) {
            currentList.removeAt(currentList.lastIndex)
        }

        // تم حل فخ التكرار: غيرنا اسم المتغير هون لـ jsonToSave عشان ما يضرب مع اللي فوق
        val jsonToSave = gson.toJson(currentList)
        sharedPreferences.edit().putString(cacheKey, jsonToSave).apply()
    }
}
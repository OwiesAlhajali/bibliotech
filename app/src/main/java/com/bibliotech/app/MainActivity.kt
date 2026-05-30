package com.bibliotech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.bibliotech.app.data.remote.RetrofitInstance
import com.bibliotech.app.data.repository.BookRepository
import com.bibliotech.app.ui.screens.search.SearchScreen
import com.bibliotech.app.ui.screens.search.SearchViewModel
import com.bibliotech.app.ui.theme.BibliotechTheme
import androidx.compose.foundation.layout.padding
import com.bibliotech.app.navigation.AppNavigation
import com.bibliotech.app.data.local.AppDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. إنشاء الـ API Service (شغال تمام عندك)
        val apiService = RetrofitInstance.api

        // 2. بناء قاعدة البيانات وجلب الـ Dao (هاد السطرين الجداد)
        val database = AppDatabase.getDatabase(this)
        val bookDao = database.bookDao()

        // 3. إنشاء الـ Repository وتمرير الـ bookDao له بدل الـ context
        val repository = BookRepository(apiService, bookDao)

        // 4. إنشاء الـ ViewModel وتمرير الـ Repository له (إذا لسا بتستخدمه هون)
        val searchViewModel = SearchViewModel(repository)

        setContent {
            BibliotechTheme {
                // تمرير الـ repository المحدث تلقائياً جوات الـ AppNavigation
                AppNavigation(repository = repository)
            }
        }
    }
}
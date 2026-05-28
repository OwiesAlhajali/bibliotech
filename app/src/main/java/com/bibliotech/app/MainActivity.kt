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
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. إنشاء الـ API Service
        val apiService = RetrofitInstance.api

        // 2. إنشاء الـ Repository وتمرير الـ API له
        val repository = BookRepository(apiService, applicationContext)

        // 3. إنشاء الـ ViewModel وتمرير الـ Repository له
        val searchViewModel = SearchViewModel(repository)

        setContent {
            BibliotechTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = searchViewModel
                    )
                }
            }
        }
    }
}
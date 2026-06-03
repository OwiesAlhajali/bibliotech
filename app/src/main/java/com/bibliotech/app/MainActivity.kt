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


        val apiService = RetrofitInstance.api


        val database = AppDatabase.getDatabase(this)
        val bookDao = database.bookDao()


        val repository = BookRepository(apiService, bookDao)


        val searchViewModel = SearchViewModel(application, repository)
        setContent {
            BibliotechTheme {

                AppNavigation(repository = repository)
            }
        }
    }
}
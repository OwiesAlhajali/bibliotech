package com.bibliotech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.bibliotech.app.data.remote.RetrofitInstance
import com.bibliotech.app.data.repository.BookRepository
import com.bibliotech.app.ui.navigation.BibliotechAppNavHost
import com.bibliotech.app.ui.theme.BibliotechTheme
import androidx.compose.foundation.layout.padding
import com.bibliotech.app.navigation.AppNavigation
import com.bibliotech.app.data.local.AppDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // enable edge-to-edge if available
        enableEdgeToEdge()

        val apiService = RetrofitInstance.api
        val database = AppDatabase.getDatabase(this)
        val bookDao = database.bookDao()
        val repository = BookRepository(apiService, bookDao)

        setContent {
            BibliotechTheme {
                // Use the new AppNavigation while keeping the legacy nav host available
                AppNavigation(repository = repository)
                // BibliotechAppNavHost() // kept for reference if needed
            }
        }
    }
}
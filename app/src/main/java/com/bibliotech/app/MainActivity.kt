package com.bibliotech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bibliotech.app.ui.navigation.BibliotechAppNavHost
import com.bibliotech.app.ui.theme.BibliotechTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BibliotechTheme {
                BibliotechAppNavHost()
            }
        }
    }
}
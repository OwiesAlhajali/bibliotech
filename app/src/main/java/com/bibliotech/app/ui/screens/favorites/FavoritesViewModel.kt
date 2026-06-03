package com.bibliotech.app.ui.screens.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.data.repository.BookRepository
import com.bibliotech.app.data.utils.AndroidDownloadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    application: android.app.Application,
    private val repository: BookRepository
) : androidx.lifecycle.AndroidViewModel(application) {


    private val bookDownloadManager = AndroidDownloadManager(application)


    val favoriteBooks: StateFlow<List<BookDoc>> = repository.getFavoriteBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    var selectedBookForSheet by mutableStateOf<BookDoc?>(null)
        private set

    var sheetDescriptionState by mutableStateOf<String>("Loading...")
        private set

    var sheetNumberOfPagesState by mutableStateOf<Int?>(null)
        private set


    fun onRemoveFavoriteClicked(book: BookDoc) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavoriteBook(book)
        }
    }


    fun openBookDetailsSheet(book: BookDoc) {
        selectedBookForSheet = book
        sheetDescriptionState = "Loading..."
        sheetNumberOfPagesState = null

        viewModelScope.launch {
            try {
                val cleanKey = book.key.removePrefix("/")
                val details = repository.getBookDetails(cleanKey)

                sheetDescriptionState = if (!details.description.isNullOrBlank()) {
                    details.description
                } else {
                    "No description available for this book."
                }
                sheetNumberOfPagesState = details.numberOfPages
            } catch (e: Exception) {
                sheetDescriptionState = "Failed to load description. Please try again."
            }
        }
    }

    fun closeBookDetailsSheet() {
        selectedBookForSheet = null
    }


    fun downloadBook(book: BookDoc) {
        val downloadUrl = "https://archive.org/download/2016TheLinuxCommandLine/2016_the-linux-command-line-a-complete-introduction.pdf"
        try {
            bookDownloadManager.downloadBook(url = downloadUrl, bookTitle = book.title)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
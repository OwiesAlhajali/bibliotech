package com.bibliotech.app.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.data.repository.BookRepository
import com.bibliotech.app.data.utils.AndroidDownloadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    object Empty : SearchUiState
    data class Success(val books: List<BookDoc>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    application: android.app.Application,
    private val repository: BookRepository
) : androidx.lifecycle.AndroidViewModel(application) {

    private val bookDownloadManager = AndroidDownloadManager(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()


    val recentBooks: StateFlow<List<BookDoc>> = repository.getRecentBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


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

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(150L)
                .distinctUntilChanged()
                .filter { query -> query.trim().length >= 4 }
                .collectLatest { query -> performSearch(query) }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                val books = repository.searchBooks(query)
                if (books.isEmpty()) {
                    _uiState.value = SearchUiState.Empty
                } else {
                    _uiState.value = SearchUiState.Success(books)
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.message ?: "An error occurred")
            }
        }
    }


    fun onBookClicked(book: BookDoc) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveBookToCache(book)
        }
    }


    fun onFavoriteToggleClicked(book: BookDoc) {
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
        val downloadUrl = "https://ia800802.us.archive.org/25/items/2016TheLinuxCommandLine/2016_the-linux-command-line_a-complete-introduction.pdf"
        try {
            bookDownloadManager.downloadBook(url = downloadUrl, bookTitle = book.title)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
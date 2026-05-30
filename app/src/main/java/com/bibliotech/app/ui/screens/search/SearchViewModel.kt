package com.bibliotech.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.data.repository.BookRepository
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
    private val repository: BookRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // 1. مراقبة كاش الكتب الأخيرة
    val recentBooks: StateFlow<List<BookDoc>> = repository.getRecentBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. المراقبة الحية واللحظية لقائمة المفضلة من الـ Room DB
    val favoriteBooks: StateFlow<List<BookDoc>> = repository.getFavoriteBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    // 3. دالة كبسة الكتاب (تم فصلها وتنظيفها)
    fun onBookClicked(book: BookDoc) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveBookToCache(book)
        }
    }

    // 4. دالة التبديل عند الضغط على القلب (مكتوبة بشكل مستقل وصحيح هنا)
    fun onFavoriteToggleClicked(book: BookDoc) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavoriteBook(book)
        }
    }
}
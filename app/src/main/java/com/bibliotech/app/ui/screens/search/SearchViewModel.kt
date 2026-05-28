package com.bibliotech.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.data.repository.BookRepository
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

    private val _recentBooks = MutableStateFlow<List<BookDoc>>(emptyList())
    val recentBooks: StateFlow<List<BookDoc>> = _recentBooks.asStateFlow()

    init {
        // قراءة القائمة المبدئية عند فتح الشاشة لأول مرة
        updateRecentBooksList()

        viewModelScope.launch {
            _searchQuery
                .debounce(1500L)
                .distinctUntilChanged()
                .filter { query -> query.trim().length >= 4 }
                .collectLatest { query -> performSearch(query) }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
            updateRecentBooksList() // تحديث وعرض القائمة المدمجة فور مسح نص البحث
        }
    }

    // جلب القائمة المحدثة دائماً من الـ Repository
    private fun updateRecentBooksList() {
        _recentBooks.value = repository.getRecentBooks()
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
        repository.saveBookToCache(book)
        updateRecentBooksList() // تحديث الـ Grid فوراً بالترتيب الجديد بعد الضغط
    }
}
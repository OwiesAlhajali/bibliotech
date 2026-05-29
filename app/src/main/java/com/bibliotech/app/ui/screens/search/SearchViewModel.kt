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

    // تعديل هندسي: تحويل الكاش لـ StateFlow يقرأ تلقائياً من الـ Repository بخلفية مريحة
    val recentBooks: StateFlow<List<BookDoc>> = repository.getRecentBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // التطبيق بيفتح فوراً بقائمة فاضية بصفر ثانية تأخير
        )

    init {
        // منطق البحث المطور مسبقاً (شغال تمام بالخلفية)
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
            // ما عاد بحاجة نستدعي دالة التحديث هون لأن الـ StateFlow بيراقب التغيير تلقائياً!
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
        // تشغيل الحفظ بخيط خلفي مريح حتى لا يسبب أي تعليق بالواجهة
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveBookToCache(book)
            // بمجرد الحفظ بالـ SharedPreferences، الـ Flow في الـ Repository رح يلقط التحديث
            // ويعكسه فوراً على الـ UI بدون ما نضطر نستدعي دوال تحديث يدوية!
        }
    }
}
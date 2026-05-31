package com.bibliotech.app.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    // 🔥 تتبع الكتاب الحالي اللي ضغط عليه المستخدم لعرضه بالـ Bottom Sheet 🔥
    var selectedBookForSheet by mutableStateOf<BookDoc?>(null)
        private set

    // 🔥 تتبع حالة الوصف الجاي من الـ API (إذا كان عم يحمل، أو خلص، أو فاضي) 🔥
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
    // 🔥 دالة تفتح الـ Sheet وتبدأ تجلب تفاصيل الكتاب فوراً بخلفية الكوروتين 🔥
    fun openBookDetailsSheet(book: BookDoc) {
        selectedBookForSheet = book
        sheetDescriptionState = "Loading..." // إعادة تعيين النص الافتراضي أثناء التحميل
        sheetNumberOfPagesState = null

        viewModelScope.launch {
            try {
                // تنظيف الـ key من كلمة "/works/" لو كانت جاي كاملة من الـ API
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

    // 🔥 دالة لإغلاق الـ Sheet وتصفير البيانات الفتح القادم 🔥
    fun closeBookDetailsSheet() {
        selectedBookForSheet = null
    }
}
package com.bibliotech.app.ui.screens.favorites // اسم الباكيدج الجديد والمستقل

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.data.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: BookRepository
) : ViewModel() {

    // قراءة مستمرة لقائمة المفضلة وتحويلها لـ StateFlow بخلفية مأمنة وبصفر ثانية تأخير
    val favoriteBooks: StateFlow<List<BookDoc>> = repository.getFavoriteBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // 🔥 تتبع الكتاب الحالي المختار في شاشة المفضلة 🔥
    var selectedBookForSheet by mutableStateOf<BookDoc?>(null)
        private set

    var sheetDescriptionState by mutableStateOf<String>("Loading...")
        private set

    var sheetNumberOfPagesState by mutableStateOf<Int?>(null)
        private set

    // دالة حذف أو تبديل حالة الكتاب من داخل شاشة المفضلة
    fun onRemoveFavoriteClicked(book: BookDoc) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavoriteBook(book)
        }
    }
    // 🔥 جلب تفاصيل الكتاب عند الضغط عليه من شاشة المفضلة 🔥
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
}


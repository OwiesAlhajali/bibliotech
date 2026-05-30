package com.bibliotech.app.ui.screens.favorites // اسم الباكيدج الجديد والمستقل

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

    // دالة حذف أو تبديل حالة الكتاب من داخل شاشة المفضلة
    fun onRemoveFavoriteClicked(book: BookDoc) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavoriteBook(book)
        }
    }
}


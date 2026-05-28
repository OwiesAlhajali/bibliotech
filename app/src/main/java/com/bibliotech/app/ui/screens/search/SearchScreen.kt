package com.bibliotech.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bibliotech.app.R
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.ui.theme.PlaceholderBg
import com.bibliotech.app.ui.theme.Purple40

// تعديل اللون الموف الخاص بالتطبيق (يمكنك استبداله بـ MaterialTheme.colorScheme.primary إذا كان معرفاً مسبقاً)


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val recentBooks by viewModel.recentBooks.collectAsState()

    Scaffold(
        bottomBar = {
            // المكون المفصول لشريط التنقل السفلي المحجوز للمستقبل
            BottomNavigationBar()
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // 1. المكون المفصول للرأس (اللوغو + النص الملهم)
            HeaderSection()

            // 2. حجز مساحة مرنة ليدفع شريط البحث ليكون في "أخر الثلث الأول" من الشاشة
            Spacer(modifier = Modifier.weight(0.2f))

            // 3. المكون المفصول لشريط البحث ذو الحواف المضيئة والمقوسة
            CustomSearchBar(
                query = searchQuery,
                onQueryChanged = { viewModel.onQueryChanged(it) }
            )

            // مسافة ثابتة تحت شريط البحث قبل بدء عرض النتائج أو الـ Grid
            Spacer(modifier = Modifier.height(24.dp))

            // 4. مساحة عرض المحتوى الديناميكي (باقي الثلثين من الشاشة)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                when (val state = uiState) {
                    is SearchUiState.Idle -> {
                        RecentSearchesSection(
                            recentBooks = recentBooks,
                            onBookClick = { viewModel.onBookClicked(it) }
                        )
                    }
                    is SearchUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center // يضمن السنترة بالمنتصف تماماً فوق وتحت ويمين ويسار
                        ) {
                            CircularProgressIndicator(color = Purple40)
                        }
                    }
                    is SearchUiState.Empty -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center // سنترة النص في منتصف الشاشة بالملي
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_results),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // جعل اللون خفيف وأنيق بحالة عدم وجود نتائج
                            )
                        }
                    }
                    is SearchUiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is SearchUiState.Success -> {
                        SearchResultsSection(
                            books = state.books,
                            onBookClick = { viewModel.onBookClicked(it) }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// مكونات واجهة المستخدم المفصولة والمطابقة للمعاير (Sub-Composables)
// ==========================================

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📚", // أيقونة مؤقتة كلوغو للتطبيق
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Bibliotech",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Purple40
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Search, Read and Enjoy",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Purple40.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        label = { Text(text = stringResource(id = R.string.search_hint)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Purple40) },
        singleLine = true,
        shape = RoundedCornerShape(24.dp), // حواف دائرية أنيقة ومقوسة بالكامل
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Purple40,       // الحواف تضيء بالموف عند التركيز والكتابة
            unfocusedBorderColor = Purple40.copy(alpha = 0.4f),
            focusedLabelColor = Purple40
        ),
        modifier = Modifier
            .fillMaxWidth()
            // إضافة ستايل إضافي كإطار مضيء خفيف حول الحواف
            .border(1.dp, Purple40.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
    )
}

@Composable
fun RecentSearchesSection(
    recentBooks: List<BookDoc>,
    onBookClick: (BookDoc) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Top / Recent Searches",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 3 يمين و 3 يسار بالتتالي
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(recentBooks) { book ->
                BookGridItem(book = book, onClick = { onBookClick(book) })
            }
        }
    }
}

@Composable
fun SearchResultsSection(
    books: List<BookDoc>,
    onBookClick: (BookDoc) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { book ->
            BookRowItem(book = book, onClick = { onBookClick(book) })
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* شاشة البحث الحالية */ },
            icon = { Icon(Icons.Default.Search, contentDescription = null, tint =Purple40) },
            label = { Text("Search") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* مجهز لإضافة قائمة المفضلة لاحقاً */ },
            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
            label = { Text("Favorites") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* مجهز لإضافة شاشة الحساب لاحقاً */ },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Account") }
        )
    }
}

@Composable
fun BookGridItem(book: BookDoc, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (book.cover_i != null) {
                AsyncImage(
                    model = "https://covers.openlibrary.org/b/id/${book.cover_i}-S.jpg",
                    contentDescription = stringResource(id = R.string.cover_description, book.title),
                    modifier = Modifier
                        .size(width = 40.dp, height = 60.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 60.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(PlaceholderBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📖", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                book.author_name?.let { authors ->
                    Text(
                        text = authors.firstOrNull() ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun BookRowItem(book: BookDoc, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (book.cover_i != null) {
                AsyncImage(
                    model = "https://covers.openlibrary.org/b/id/${book.cover_i}-S.jpg",
                    contentDescription = stringResource(id = R.string.cover_description, book.title),
                    modifier = Modifier
                        .size(width = 50.dp, height = 75.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 75.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PlaceholderBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📖", style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                book.author_name?.let { authors ->
                    Text(
                        text = "By: ${authors.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
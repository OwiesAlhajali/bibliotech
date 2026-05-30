package com.bibliotech.app.ui.screens.search

import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bibliotech.app.R
import com.bibliotech.app.data.remote.BookDoc
import com.bibliotech.app.ui.theme.*

@Composable
fun SearchScreen(
    navController: NavController, // استقبال الـ Controller الرسمي
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
){
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val recentBooks by viewModel.recentBooks.collectAsState()
    val favoriteBooks by viewModel.favoriteBooks.collectAsState()
    androidx.activity.compose.BackHandler(enabled = searchQuery.isNotEmpty() || uiState !is SearchUiState.Idle) {
        // عند الضغط على رجوع: فضّي نص البحث ورجّع الشاشة للوضع الافتراضي
        viewModel.onQueryChanged("")
    }
    Scaffold(
        bottomBar = {
            // البوتوم بار هنا مربوط بالـ NavController الفعلي للتنقل
            NavigationBar(containerColor = Color.White, modifier = Modifier.shadow(elevation = 8.dp)) {
                NavigationBarItem(
                    selected = true, // شاشة البحث نشطة
                    onClick = { },
                    icon = { Icon(Icons.Default.Search, contentDescription = null, tint = ModernPurple) },
                    label = { Text("Search", color = ModernPurple, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        // تنقل آمن لشاشة المفضلة بدون تكرار الـ Stack
                        navController.navigate("favorites_screen") {
                            popUpTo("search_screen") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Account") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SolidPurpleBg)
                .padding(innerPadding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                HeaderSectionWithAnimation()
                Spacer(modifier = Modifier.height(16.dp))
                CustomModernSearchBar(query = searchQuery, onQueryChanged = { viewModel.onQueryChanged(it) })
                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                    when (val state = uiState) {
                        // جوات الـ when (val state = uiState) عند حالة الـ Idle:
                        is SearchUiState.Idle -> {
                            RecentSearchesSection(
                                recentBooks = recentBooks,
                                favoriteBooks = favoriteBooks, // مررها هنا كمان لتصل للدالة تحت
                                onBookClick = { viewModel.onBookClicked(it) },
                                onFavoriteToggle = { viewModel.onFavoriteToggleClicked(it) }
                            )
                        }
                        is SearchUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = ModernPurple)
                            }
                        }
                        is SearchUiState.Empty -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = stringResource(id = R.string.no_results), color = ModernPurple.copy(alpha = 0.6f))
                            }
                        }
                        is SearchUiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            }
                        }
                        is SearchUiState.Success -> {
                            SearchResultsSection(
                                books = state.books,
                                favoriteBooks = favoriteBooks,
                                onBookClick = { viewModel.onBookClicked(it) },
                                onFavoriteToggle = { viewModel.onFavoriteToggleClicked(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun HeaderSectionWithAnimation() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .height(95.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "📚", fontSize = 32.sp, modifier = Modifier.padding(end = 8.dp))
            Text(
                text = "Bibliotech",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ModernPurple
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Search, Read and Enjoy",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = ModernPurple.copy(alpha = 0.8f),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun CustomModernSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        label = { Text(text = stringResource(id = R.string.search_hint)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ModernPurple) },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ModernPurple,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = ModernPurple
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(28.dp))
            .border(1.dp, ModernPurple.copy(alpha = 0.15f), RoundedCornerShape(28.dp))
    )
}

@Composable
fun RecentSearchesSection(
    recentBooks: List<BookDoc>,
    favoriteBooks: List<BookDoc>, // تأكد إنها بتستقبل القائمة الحية هون
    onBookClick: (BookDoc) -> Unit,
    onFavoriteToggle: (BookDoc) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Top / Recent Searches",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 14.dp, start = 4.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(recentBooks, key = { it.key }) { book ->
                // 1. فحص لحظي إذا كان الكتاب الحالي موجود بالمفضلة أولا
                val isBookFav = favoriteBooks.any { it.key == book.key }

                // 2. تمرير البارامترات كاملة للمربع عشان يقلب اللون فوراً
                ModernBookGridItem(
                    book = book,
                    isFavorite = isBookFav, // تمرير الحالة الحية
                    onClick = { onBookClick(book) },
                    onFavoriteToggle = { onFavoriteToggle(book) } // تمرير كبسة القلب
                )
            }
        }
    }
}

@Composable
fun ModernBookGridItem(
    book: BookDoc,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() }
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardBg)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (book.cover_i != null ) {
                    AsyncImage(
                        model = "https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg",
                        contentDescription = null,
                        modifier = Modifier.size(width = 45.dp, height = 68.dp).clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop,

                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 45.dp, height = 68.dp) // نفس حجم الـ AsyncImage بالظبط
                            .clip(RoundedCornerShape(6.dp))      // نفس الحواف المنحنية
                            .background(Color(0xFFF0F2F5)),     // الرمادي الفيسبوكي الناعم
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Book,
                            contentDescription = "No Cover Available",
                            modifier = Modifier.size(24.dp),    // صغّرنا الأيقونة شوي لتناسب حجم الـ Box الصغير
                            tint = Color(0xFF8A8D91)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f).padding(end = 24.dp)) {
                    Text(
                        text = book.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        color = Color.Black.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    book.author_name?.let { authors ->
                        Text(
                            text = authors.firstOrNull() ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            color = ModernPurple.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (isFavorite) ModernPurple else Color.LightGray
                )
            }
        }
    }
}

@Composable
fun SearchResultsSection(
    books: List<BookDoc>,
    favoriteBooks: List<BookDoc>,
    onBookClick: (BookDoc) -> Unit,
    onFavoriteToggle: (BookDoc) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(books, key = { it.key }) { book ->
            val isBookFav = favoriteBooks.any { it.key == book.key }
            ModernBookRowItem(
                book = book,
                isFavorite = isBookFav,
                onClick = { onBookClick(book) },
                onFavoriteToggle = { onFavoriteToggle(book) }
            )
        }
    }
}

@Composable
fun ModernBookRowItem(
    book: BookDoc,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardBg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (book.cover_i != null) {
                AsyncImage(
                    model = "https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg",
                    contentDescription = null,
                    modifier = Modifier.size(width = 52.dp, height = 78.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 45.dp, height = 68.dp) // نفس حجم الـ AsyncImage بالظبط
                        .clip(RoundedCornerShape(6.dp))      // نفس الحواف المنحنية
                        .background(Color(0xFFF0F2F5)),     // الرمادي الفيسبوكي الناعم
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Book,
                        contentDescription = "No Cover Available",
                        modifier = Modifier.size(24.dp),    // صغّرنا الأيقونة شوي لتناسب حجم الـ Box الصغير
                        tint = Color(0xFF8A8D91)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                book.author_name?.let { authors ->
                    Text(
                        text = "By: ${authors.joinToString(", ")}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ModernPurple.copy(alpha = 0.7f)
                    )
                }
            }

            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (isFavorite) ModernPurple else Color.LightGray
                )
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.shadow(elevation = 8.dp)
    ) {
        NavigationBarItem(
            selected = true, // شاشة البحث هي النشطة حالياً
            onClick = {
                // إذا ضغط ع السيرش وهو جواه ما يعمل شي
            },
            icon = { Icon(Icons.Default.Search, contentDescription = null, tint = ModernPurple) },
            label = { Text("Search", color = ModernPurple, fontWeight = FontWeight.Bold) }
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                // الانتقال السحري لشاشة المفضلة (حسب اسم الـ Route عندك بالـ NavHost)
                navController.navigate("favorites_screen")
            },
            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
            label = { Text("Favorites") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* التنقل للأكاونت لاحقاً */ },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Account") }
        )
    }
}
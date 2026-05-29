package com.bibliotech.app.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bibliotech.app.R
import com.bibliotech.app.data.remote.BookDoc

// الألوان الحديثة المحدثة للواجهة الفخمة
val PurpleGradientStart = Color(0xFFFFFFFF)
val PurpleGradientEnd = Color(0xFFF3E5F5) // تدرج خفيف جداً للموف الساحر بأسفل الشاشة
val ModernPurple = Color(0xFF7B1FA2)
val GlassCardBg = Color(0x99FFFFFF) // خلفية زجاجية شبه شفافة للكروت

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val recentBooks by viewModel.recentBooks.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        // تطبيق الخلفية الديناميكية الممتدة بالتدرج اللوني (Gradient) لمنح عمق بصري فخم
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PurpleGradientStart, PurpleGradientEnd)
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // 1. الرأس المعزول مع الإنيميشن الحديث للكلمات
                HeaderSectionWithAnimation()

                Spacer(modifier = Modifier.weight(0.15f))

                // 2. شريط البحث المطور بظلال ناعمة (Drop Shadow) مريحة للعين
                CustomModernSearchBar(
                    query = searchQuery,
                    onQueryChanged = { viewModel.onQueryChanged(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. مساحة عرض المحتوى الديناميكي
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
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = ModernPurple)
                            }
                        }
                        is SearchUiState.Empty -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.no_results),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = ModernPurple.copy(alpha = 0.6f)
                                )
                            }
                        }
                        is SearchUiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
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
}

@Composable
fun HeaderSectionWithAnimation() {
    var isVisible by remember { mutableStateOf(false) }

    // إطلاق الإنيميشن فوراً عند بناء الواجهة لأول مرة (Enter Animation)
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
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

        Spacer(modifier = Modifier.height(10.dp))

        // إنيميشن صعود وظهور كلمات "Search, Read and Enjoy" بشكل انسيابي حديث
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)) +
                    slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 800)
                    )
        ) {
            Text(
                text = "Search, Read and Enjoy",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = ModernPurple.copy(alpha = 0.8f),
                letterSpacing = 0.5.sp
            )
        }
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
            // المسميات الصحيحة المتوافقة مع إصدار Material 3 عندك
            focusedBorderColor = ModernPurple,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = ModernPurple
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(28.dp))
            .border(1.dp, ModernPurple.copy(alpha = 0.15f), RoundedCornerShape(28.dp))
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
            items(recentBooks) { book ->
                ModernBookGridItem(book = book, onClick = { onBookClick(book) })
            }
        }
    }
}

@Composable
fun ModernBookGridItem(book: BookDoc, onClick: () -> Unit) {
    // كرت زجاجي حديث (Glassmorphic Card) بحواف ناعمة مريحة جداً للعين
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (book.cover_i != null) {
                AsyncImage(
                    model = "https://covers.openlibrary.org/b/id/${book.cover_i}-S.jpg",
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 45.dp, height = 68.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 45.dp, height = 68.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ModernPurple.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📖", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
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
    }
}

@Composable
fun SearchResultsSection(
    books: List<BookDoc>,
    onBookClick: (BookDoc) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(books) { book ->
            ModernBookRowItem(book = book, onClick = { onBookClick(book) })
        }
    }
}

@Composable
fun ModernBookRowItem(book: BookDoc, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassCardBg)
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
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 52.dp, height = 78.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 52.dp, height = 78.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ModernPurple.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📖", fontSize = 22.sp)
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
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.9f),
        modifier = Modifier.shadow(elevation = 16.dp)
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Search, contentDescription = null, tint = ModernPurple) },
            label = { Text("Search", color = ModernPurple, fontWeight = FontWeight.Bold) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
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
package com.bibliotech.app.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bibliotech.app.ui.screens.search.ModernBookGridItem
import com.bibliotech.app.ui.theme.GlassCardBg
import com.bibliotech.app.ui.theme.ModernPurple
import com.bibliotech.app.ui.theme.SolidPurpleBg

@Composable
fun FavoritesScreen(
    navController: NavController, // استقبال الـ Controller
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val favoriteBooks by viewModel.favoriteBooks.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, modifier = Modifier.shadow(elevation = 8.dp)) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("search_screen") {
                            popUpTo("search_screen") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = true, // شاشة المفضلة نشطة هنا
                    onClick = { },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = ModernPurple) },
                    label = { Text("Favorites", color = ModernPurple, fontWeight = FontWeight.Bold) }
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
            modifier = modifier
                .fillMaxSize()
                .background(SolidPurpleBg)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "My Favorite Books",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ModernPurple,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (favoriteBooks.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(text = "No favorite books added yet", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(favoriteBooks, key = { it.key }) { book ->
                            ModernBookGridItem(
                                book = book,
                                isFavorite = true,
                                onClick = {viewModel.openBookDetailsSheet(book) },
                                onFavoriteToggle = { viewModel.onRemoveFavoriteClicked(book) }
                            )
                        }
                    }
                }
            }
        }
    }
    viewModel.selectedBookForSheet?.let { selectedBook ->
        com.bibliotech.app.ui.components.BookDetailsBottomSheet(
            book = selectedBook,
            description = viewModel.sheetDescriptionState,
            numberOfPages = viewModel.sheetNumberOfPagesState,
            onDismiss = { viewModel.closeBookDetailsSheet() } ,// إغلاق الـ Sheet عند السحب لأسفل
            onDownloadClick = { book -> viewModel.downloadBook(book) },
            onBrowseClick = { bookKey ->
                // 👈 هنا بنادي الـ navController بكل راحة لأن الشاشة الكبيرة بتشوفه
                navController.navigate("book_reader?bookKey=$bookKey")
            }
        )
    }
}
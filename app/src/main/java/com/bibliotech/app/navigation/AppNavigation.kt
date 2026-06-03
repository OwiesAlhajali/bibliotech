package com.bibliotech.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bibliotech.app.data.repository.BookRepository
import com.bibliotech.app.ui.screens.bookreader.BookReaderScreen
import com.bibliotech.app.ui.screens.favorites.FavoritesScreen
import com.bibliotech.app.ui.screens.favorites.FavoritesViewModel
import com.bibliotech.app.ui.screens.search.SearchScreen
import com.bibliotech.app.ui.screens.search.SearchViewModel

object Screen {
    const val Search = "search_screen"
    const val Favorites = "favorites_screen"
}

@Composable
fun AppNavigation(
    repository: BookRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as android.app.Application

    NavHost(
        navController = navController,
        startDestination = Screen.Search,
        modifier = modifier
    ) {

        composable(Screen.Search) {
            val searchViewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {

                        return SearchViewModel(application, repository) as T
                    }
                }
            )
            SearchScreen(navController = navController, viewModel = searchViewModel)
        }


        composable(Screen.Favorites) {
            val favoritesViewModel: FavoritesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return FavoritesViewModel(application, repository) as T
                    }
                }
            )
            FavoritesScreen(navController = navController, viewModel = favoritesViewModel)
        }

        composable(
            route = "book_reader?bookKey={bookKey}",
            arguments = listOf(
                navArgument("bookKey") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->


            val bookKey = backStackEntry.arguments?.getString("bookKey") ?: ""
            BookReaderScreen(
                bookId = bookKey,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
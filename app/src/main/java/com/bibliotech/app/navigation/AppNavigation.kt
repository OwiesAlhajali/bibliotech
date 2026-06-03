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
import com.bibliotech.app.ui.auth.AuthChoiceScreen
import com.bibliotech.app.ui.auth.LoginScreen
import com.bibliotech.app.ui.auth.SignUpScreen
import com.bibliotech.app.ui.splash.BibliotechSplashRoute
import com.google.firebase.auth.FirebaseAuth

object Screen {
    const val Search = "search_screen"
    const val Favorites = "favorites_screen"
    const val Splash = "splash"
    const val AuthChoice = "auth_choice"
    const val Login = "login"
    const val SignUp = "signup"
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
        startDestination = Screen.Splash,
        modifier = modifier
    ) {

        composable(Screen.Splash) {
            BibliotechSplashRoute { 
                // After splash, go to auth if not signed in, otherwise go to search
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    navController.navigate(Screen.Search) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.AuthChoice) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.AuthChoice) {
            AuthChoiceScreen(
                onLoginClick = { navController.navigate(Screen.Login) },
                onSignUpClick = { navController.navigate(Screen.SignUp) },
                onGuestClick = {
                    navController.navigate(Screen.Search) {
                        popUpTo(Screen.AuthChoice) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onCreateAccountClick = { navController.navigate(Screen.SignUp) },
                onSuccess = {
                    navController.navigate(Screen.Search) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate(Screen.Login) },
                onSuccess = {
                    navController.navigate(Screen.Search) {
                        popUpTo(Screen.SignUp) { inclusive = true }
                    }
                }
            )
        }

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
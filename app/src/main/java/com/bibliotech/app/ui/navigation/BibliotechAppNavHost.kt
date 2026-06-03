package com.bibliotech.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.bibliotech.app.ui.auth.AuthChoiceScreen
import com.bibliotech.app.ui.auth.LoginScreen
import com.bibliotech.app.ui.auth.SignUpScreen
import com.bibliotech.app.ui.home.HomeScreen
import com.bibliotech.app.ui.splash.BibliotechSplashRoute
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bibliotech.app.ui.auth.AuthViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity

@Composable
fun BibliotechAppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = BibliotechRoutes.Splash
    ) {
        composable(BibliotechRoutes.Splash) {
            BibliotechSplashRoute(
                onSplashFinished = {
                    navController.navigate(
                        route = BibliotechRoutes.Auth,
                        navOptions = navOptions {
                            popUpTo(BibliotechRoutes.Splash) { inclusive = true }
                            launchSingleTop = true
                        }
                    )
                }
            )
        }

        composable(BibliotechRoutes.Auth) {
            AuthChoiceScreen(
                onLoginClick = { navController.navigate(BibliotechRoutes.Login) },
                onSignUpClick = { navController.navigate(BibliotechRoutes.SignUp) },
                onGuestClick = { navController.navigate(BibliotechRoutes.Home) }
            )
        }

        composable(BibliotechRoutes.Login) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onCreateAccountClick = { navController.navigate(BibliotechRoutes.SignUp) },
                onSuccess = {
                    navController.navigate(BibliotechRoutes.Home) {
                        popUpTo(BibliotechRoutes.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(BibliotechRoutes.SignUp) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate(BibliotechRoutes.Login) },
                onSuccess = {
                    navController.navigate(BibliotechRoutes.Home) {
                        popUpTo(BibliotechRoutes.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(BibliotechRoutes.Home) {
            val activity = LocalContext.current as ComponentActivity
            val authViewModel: AuthViewModel = viewModel(activity)
            HomeScreen(
                onLogoutClick = {
                    authViewModel.signOut()
                    navController.navigate(BibliotechRoutes.Auth) {
                        popUpTo(BibliotechRoutes.Home) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
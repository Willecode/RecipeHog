package com.portfolio.recipehog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.portfolio.auth.presentation.intro.IntroScreenRoot
import com.portfolio.auth.presentation.login.LoginScreenRoot
import com.portfolio.auth.presentation.register.RegisterAction
import com.portfolio.auth.presentation.register.RegisterScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "content" else "auth"
    ) {
        authGraph(navController)
        contentGraph(navController)
    }
}

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = "intro",
        route = "auth"
    ) {
        composable(route = "intro") {
            IntroScreenRoot(
                onRegisterClick = {navController.navigate(("register"))},
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable(route = "login") {
            LoginScreenRoot(
                onRegisterClicked = {
                    navController.navigate("register") {
                        popUpTo("login") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulLogin = {
                    navController.navigate("content") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = "register") {
            RegisterScreenRoot(
                onLoginClicked = {
                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegister = {
                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.contentGraph(navController: NavHostController) {
    navigation(
        startDestination = "home",
        route = "content"
    ) {
        composable(route = "home") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(text = "User's home page :)")
            }
        }
    }
}
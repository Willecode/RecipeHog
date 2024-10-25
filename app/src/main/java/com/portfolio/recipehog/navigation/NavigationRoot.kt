package com.portfolio.recipehog.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.portfolio.auth.presentation.intro.IntroScreenRoot
import com.portfolio.auth.presentation.login.LoginScreenRoot
import com.portfolio.auth.presentation.register.RegisterScreenRoot
import com.portfolio.auth.presentation.userinfo.UserInfoScreenRoot
import com.portfolio.bookmarks.presentation.BookmarksScreenRoot
import com.portfolio.home.presentation.HomeScreenRoot
import com.portfolio.presentation.DiscoverScreenRoot
import com.portfolio.profile.presentation.edit_profile.EditProfileScreenRoot
import com.portfolio.profile.presentation.view_profile.ViewProfileScreenRoot
import com.portfolio.recipe.presentation.create_recipe.CreateRecipeScreenRoot
import com.portfolio.recipe.presentation.view_recipe.ViewRecipeScreenRoot
import com.portfolio.recipehog.MainViewModel
import com.portfolio.review.presentation.ReviewScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = if (viewModel.state.isLoggedIn && viewModel.hasUsername())
            DestinationContent else DestinationAuth
    ) {
        authGraph(navController, viewModel)
        contentGraph(navController, viewModel)
    }
}

fun NavGraphBuilder.authGraph(navController: NavHostController, viewModel: MainViewModel) {
    navigation<DestinationAuth>(
        startDestination = if (viewModel.state.isLoggedIn) DestinationUserInfo else DestinationIntro
    ) {
        composable<DestinationIntro> {
            IntroScreenRoot(
                onRegisterClick = {navController.navigate(route = DestinationRegister)},
                onLoginClick = { navController.navigate(route = DestinationLogin) }
            )
        }
        composable<DestinationLogin> {
            LoginScreenRoot(
                onRegisterClicked = {
                    navController.navigate(route = DestinationRegister) {
                        popUpTo(route = DestinationLogin) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulLogin = {
                    if (viewModel.hasUsername()) {
                        navController.navigate(route = DestinationContent) {
                            popUpTo(route = DestinationAuth) {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate(route = DestinationUserInfo) {
                            popUpTo(route = DestinationLogin) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
        composable<DestinationRegister> {
            RegisterScreenRoot(
                onLoginClicked = {
                    navController.navigate(route = DestinationLogin) {
                        popUpTo(route = DestinationRegister) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegister = {
                    navController.navigate(route = DestinationLogin) {
                        popUpTo(route = DestinationRegister) {
                            inclusive = true
                        }
                        restoreState = true
                    }
                }
            )
        }

        composable<DestinationUserInfo> {
            UserInfoScreenRoot (
                onUserInfoUpdateSuccess = {
                    navController.navigate(route = DestinationContent) {
                        popUpTo(route = DestinationIntro) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.contentGraph(navController: NavHostController, viewModel: MainViewModel) {
    navigation<DestinationContent>(
        startDestination = DestinationHome
    ) {
        fun logOutUser() {
            viewModel.logOut(
                onSuccessfulLogout = {
                    navController.navigate(route = DestinationAuth){
                        popUpTo(route = DestinationContent) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        fun navigateToViewRecipe(recipeId: String) {
            navController.navigate(DestinationViewRecipe(recipeId = recipeId)) {
                launchSingleTop = true
            }
        }

        fun navigateToViewUser(userId: String) {
            navController.navigate(DestinationViewUser(userId = userId)) {
                launchSingleTop = true
            }
        }

        fun navigateToReviews(recipeId: String) {
            navController.navigate(DestinationReviews(reviewRecipeId = recipeId)) {
                launchSingleTop = true
            }
        }

        composable<DestinationHome> {
            HogNavigationSuiteScaffold(
                navController = navController
            ) {
                HomeScreenRoot(
                    onRecipeClick = {recipeId ->
                        navigateToViewRecipe(recipeId)
                    },
                    onLogoutClick = {
                        logOutUser()
                    },
                    onAuthError = {
                        logOutUser()
                    }
                )
            }
        }
        composable<DestinationViewRecipe> {
            ViewRecipeScreenRoot(
                onBackPress = { navController.popBackStack() },
                onAuthError = { logOutUser() },
                onAuthorClick = { authorId -> navigateToViewUser(userId = authorId)},
                onReviewsClicked = {recipeId -> navigateToReviews(recipeId)}
            )
        }
        composable<DestinationCreateRecipe> {
            CreateRecipeScreenRoot(
                onSuccessfullyPosted = {
                    navController.popBackStack()
                },
                onAuthError = { logOutUser() }
            )
        }
        composable<DestinationDiscover> {
            HogNavigationSuiteScaffold(
                navController = navController
            ) {
                DiscoverScreenRoot(
                    onRecipeClicked = {recipeId ->
                        navigateToViewRecipe(recipeId)
                    },
                    onAuthError = { logOutUser() }
                )
            }
        }
        composable<DestinationBookmarks> {
            HogNavigationSuiteScaffold(
                navController = navController
            ) {
                BookmarksScreenRoot(
                    onRecipeClick = {recipeId ->
                        navigateToViewRecipe(recipeId)
                    },
                    onAuthError = { logOutUser() }
                )
            }
        }
        composable<DestinationYou> {
            EditProfileScreenRoot(
                onRecipeClick = { recipeId -> navigateToViewRecipe(recipeId = recipeId) },
                onAuthError = { logOutUser() }
            )
        }
        composable<DestinationViewUser> {
            ViewProfileScreenRoot(
                onRecipeClick = { recipeId -> navigateToViewRecipe(recipeId = recipeId) },
                onAuthError = { logOutUser() }
            )
        }

        composable<DestinationReviews> {
            ReviewScreenRoot(
                onUserClick = { navigateToViewUser(userId = it) },
                onAuthError = { logOutUser() }
            )
        }
    }
}
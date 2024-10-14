package com.portfolio.recipehog.navigation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.portfolio.core.presentation.designsystem.AddIcon
import com.portfolio.core.presentation.designsystem.BookmarkIcon
import com.portfolio.core.presentation.designsystem.HomeIcon
import com.portfolio.core.presentation.designsystem.PersonIcon
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.SearchIcon

@Composable
fun HogNavigationSuiteScaffold(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    content: @Composable() () -> Unit
) {
    data class NavItem(
        val label: String,
        val icon: @Composable() () -> Unit,
        val destination: Any,
        val onClick: () -> Unit = {
            navController.navigate(destination) {
                launchSingleTop = true
                restoreState = true
                popUpTo(DestinationHome) {
                    saveState = true
                }
            }
        }
    )

    val navItems = listOf(
        NavItem(
            label = "Home",
            icon = { Icon(HomeIcon, "Home") },
            destination = DestinationHome
        ),
        NavItem(
            label = "Search",
            icon = { Icon(SearchIcon, "Search") },
            destination = DestinationDiscover
        ),
        NavItem(
            label = "",
            icon = {
                HogFAB(
                    onClick = {
                        navController.navigate(DestinationCreateRecipe) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(DestinationHome) {
                                saveState = true
                            }
                        }
                    }
                )
            },
            destination = DestinationCreateRecipe
        ),
        NavItem(
            label = "Saved",
            icon = { Icon(BookmarkIcon, "Saved") },
            destination = DestinationSaved
        ),
        NavItem(
            label = "You",
            icon = { Icon(PersonIcon, "You") },
            destination = DestinationYou
        )
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            val currentDestination = navController.currentBackStackEntry?.destination
            navItems.forEachIndexed { _, navItem ->
                item(
                    icon = navItem.icon,
                    label = { Text(navItem.label) },
                    selected = currentDestination?.hierarchy?.any {
                        it.hasRoute(navItem.destination::class)
                    } == true,
                    onClick = navItem.onClick
                )
            }
        }
    ) {
        content()
    }
}

@Composable
private fun HogFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .fillMaxSize()
        ) {
            Icon(
                imageVector = AddIcon,
                contentDescription = "New Recipe",
                tint = MaterialTheme.colorScheme.onPrimary
            )

        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HogNavigationSuiteScaffoldPreview(){
    RecipeHogTheme {
        Surface {
            val navController = rememberNavController()
            HogNavigationSuiteScaffold(
                modifier = Modifier,
                navController = navController
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()

                ) {
                    Text("Hey there")
                }
            }
        }
    }
}
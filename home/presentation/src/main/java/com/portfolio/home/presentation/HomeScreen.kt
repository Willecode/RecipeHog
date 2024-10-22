package com.portfolio.home.presentation

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.core.domain.model.HomeContent
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.presentation.designsystem.DotMenuIcon
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.components.HogFetchingContentBox
import com.portfolio.core.presentation.designsystem.components.HogOnClickSearchField
import com.portfolio.core.presentation.designsystem.components.HogProgressIndicatorBox
import com.portfolio.core.presentation.designsystem.components.RecipeCarousel
import com.portfolio.core.presentation.designsystem.components.RecipeHighlightCard
import com.portfolio.core.presentation.ui.ObserveAsEvents

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = org.koin.androidx.compose.koinViewModel(),
    onRecipeClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onAuthError: () -> Unit
) {

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = viewModel.eventFlow) {event ->
        eventHandler(
            event = event,
            keyboardController = keyboardController,
            context = context,
            onAuthError = onAuthError
        )
    }

    if (viewModel.state.isLoading) {
        HogProgressIndicatorBox()
    } else {
        HomeScreen(
            state = viewModel.state,
            onAction = { action ->
                when (action) {
                    is HomeAction.OnRecipeClick -> {
                        onRecipeClick(action.recipeId)
                    }
                    HomeAction.OnSearchClick -> Unit
                    HomeAction.OnLogoutClick -> onLogoutClick()
                }
                viewModel.onAction(action)
            }
        )
    }
}

private fun eventHandler(
    event: HomeEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onAuthError: () -> Unit
) {
    when (event) {
        is HomeEvent.HomeError -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }
        HomeEvent.AuthError -> {
            onAuthError()
        }
    }
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    //TODO: Use LazyColumn
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    text = "Hello ${state.userName ?: "there"},",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.home_greeting),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 30.sp, lineHeight = 40.sp)
                )

            }
            Box(
                contentAlignment = Alignment.Center
            ) {
                var expanded by remember {mutableStateOf(false)}
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(DotMenuIcon, "Open menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.logout)) },
                        onClick = {
                            onAction(HomeAction.OnLogoutClick)
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HogOnClickSearchField(callBack = { onAction(HomeAction.OnSearchClick) })
        Spacer(modifier = Modifier.height(16.dp))
        state.contentList.forEach {content ->
            Spacer(modifier = Modifier.height(16.dp))
            when(content) {
                is HomeContent.MultiRecipePreview -> {
                    RecipeCarousel(
                        title = content.title,
                        items = content.recipes,
                        onRecipeClick = { onAction(HomeAction.OnRecipeClick(it)) }
                    )
                }
                is HomeContent.SingleRecipePreview -> {
                    RecipeHighlightCard(
                        title = content.title,
                        recipe = content.recipe,
                        onClick = { onAction(HomeAction.OnRecipeClick(content.recipe.recipeId)) })
                }
            }

        }
    }
    if (state.isFetchingContent) {
        HogFetchingContentBox()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview()
@Composable
private fun HomeScreenPreview() {
    RecipeHogTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface
        ) {
            HomeScreen(
                state = HomeState(
                    userName = "George",
                    contentList =
                    listOf(
                        HomeContent.MultiRecipePreview(
                            title = "Recents",
                            recipes = listOf(
                                RecipePreview(
                                    title = "Classic Margherita Pizza",
                                    author = "Chef Mario",
                                    description = "A simple and delicious pizza with fresh tomatoes, mozzarella, and basil. Perfect for pizza lovers!",
                                    imgUrl = "https://example.com/margherita-pizza.jpg",
                                    recipeId = "001"
                                ),
                                RecipePreview(
                                    title = "Spaghetti Bolognese",
                                    author = "Chef Luigi",
                                    description = "Traditional Italian pasta dish with rich and savory beef ragu. A family favorite for generations.",
                                    imgUrl = "https://example.com/spaghetti-bolognese.jpg",
                                    recipeId = "002"
                                ),
                                RecipePreview(
                                    title = "Avocado Toast",
                                    author = "Chef Anna",
                                    description = "Healthy and quick breakfast featuring mashed avocado, topped with cherry tomatoes and a drizzle of olive oil.",
                                    imgUrl = "https://example.com/avocado-toast.jpg",
                                    recipeId = "003"
                                ),
                                RecipePreview(
                                    title = "Beef Tacos",
                                    author = "Chef Jose",
                                    description = "Flavored beef, fresh lettuce, tomatoes, and cheddar cheese wrapped in a crispy taco shell.",
                                    imgUrl = "https://example.com/beef-tacos.jpg",
                                    recipeId = "004"
                                )
                            )
                        ),
                        HomeContent.SingleRecipePreview(
                            title = "Recipe of the day",
                            recipe = RecipePreview(
                                title = "Lemon Cheesecake",
                                author = "Chef Sophie",
                                description = "Creamy and tangy cheesecake with a graham cracker crust, topped with fresh lemon zest.",
                                imgUrl = "https://example.com/lemon-cheesecake.jpg",
                                recipeId = "005"
                            )
                        )
                    )
                ),
                onAction = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreviewFetching() {
    RecipeHogTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface
        ) {
            HomeScreen(
                state = HomeState(
                    userName = null,
                    isFetchingContent = true,
                    contentList =
                    listOf(
                        HomeContent.MultiRecipePreview(
                            title = "Recents",
                            recipes = listOf(
                                RecipePreview(
                                    title = "Classic Margherita Pizza",
                                    author = "Chef Mario",
                                    description = "A simple and delicious pizza with fresh tomatoes, mozzarella, and basil. Perfect for pizza lovers!",
                                    imgUrl = "https://example.com/margherita-pizza.jpg",
                                    recipeId = "001"
                                ),
                                RecipePreview(
                                    title = "Spaghetti Bolognese",
                                    author = "Chef Luigi",
                                    description = "Traditional Italian pasta dish with rich and savory beef ragu. A family favorite for generations.",
                                    imgUrl = "https://example.com/spaghetti-bolognese.jpg",
                                    recipeId = "002"
                                ),
                                RecipePreview(
                                    title = "Avocado Toast",
                                    author = "Chef Anna",
                                    description = "Healthy and quick breakfast featuring mashed avocado, topped with cherry tomatoes and a drizzle of olive oil.",
                                    imgUrl = "https://example.com/avocado-toast.jpg",
                                    recipeId = "003"
                                ),
                                RecipePreview(
                                    title = "Beef Tacos",
                                    author = "Chef Jose",
                                    description = "Flavored beef, fresh lettuce, tomatoes, and cheddar cheese wrapped in a crispy taco shell.",
                                    imgUrl = "https://example.com/beef-tacos.jpg",
                                    recipeId = "004"
                                )
                            )
                        ),
                        HomeContent.SingleRecipePreview(
                            title = "Recipe of the day",
                            recipe = RecipePreview(
                                title = "Lemon Cheesecake",
                                author = "Chef Sophie",
                                description = "Creamy and tangy cheesecake with a graham cracker crust, topped with fresh lemon zest.",
                                imgUrl = "https://example.com/lemon-cheesecake.jpg",
                                recipeId = "005"
                            )
                        )
                    )
                ),
                onAction = {}
            )
        }
    }
}
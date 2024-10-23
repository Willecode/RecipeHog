package com.portfolio.bookmarks.presentation

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.components.RecipeCarousel
import com.portfolio.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable

fun BookmarksScreenRoot(
    viewModel: BookmarksViewModel = koinViewModel(),
    onRecipeClick: (String) -> Unit,
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

    BookmarksScreen(
        state = viewModel.state,
        onAction = {action ->
            when (action) {
                is BookmarksAction.OnRecipeClick -> onRecipeClick(action.recipeId)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

private fun eventHandler(
    event: BookmarksEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onAuthError: () -> Unit
) {
    when (event) {
        is BookmarksEvent.BookmarksError -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }
        BookmarksEvent.AuthError -> {
            onAuthError()
        }
    }
}

@Composable
private fun BookmarksScreen(
    state: BookmarksState,
    onAction: (BookmarksAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        RecipeCarousel(
            title = stringResource(id = R.string.bookmarks),
            items = state.bookmarks,
            onRecipeClick = {onAction(BookmarksAction.OnRecipeClick(it))}
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        RecipeCarousel(
            title = stringResource(id = R.string.liked),
            items = state.liked,
            onRecipeClick = {onAction(BookmarksAction.OnRecipeClick(it))}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview()
@Composable
private fun BookmarksScreenPreview() {
    RecipeHogTheme {
        Surface {
            BookmarksScreen(
                state = BookmarksState(
                    liked = listOf(
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
                    ),
                    bookmarks = listOf(
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
                onAction = {}
            )
        }
    }
}
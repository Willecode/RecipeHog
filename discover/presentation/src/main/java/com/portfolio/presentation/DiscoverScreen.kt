package com.portfolio.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.presentation.designsystem.RecipeHogTheme


@androidx.compose.runtime.Composable

fun DiscoverScreenRoot(
    viewModel: DiscoverViewModel = org.koin.androidx.compose.koinViewModel()
) {
    val searchText by viewModel.searchText.collectAsState()
    DiscoverScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        searchText = searchText
    )
}

@androidx.compose.runtime.Composable
private fun DiscoverScreen(
    state: DiscoverState,
    onAction: (DiscoverAction) -> Unit,
    searchText: String
) {
    Column(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
    ) {

        TextField(
            value = searchText,
            onValueChange = { onAction(DiscoverAction.onSearchTextChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Search") }
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.recipePreviews) { recipePreview ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = recipePreview.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    leadingContent = {
                        AsyncImage(
                            model = recipePreview.imgUrl,
                            contentDescription = "Picture of ${recipePreview.title}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .height(50.dp)
                                .aspectRatio(1f)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = recipePreview.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    overlineContent = {
                        Text(
                            text = recipePreview.author,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }


                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@androidx.compose.runtime.Composable
private fun DiscoverScreenPreview() {
    RecipeHogTheme {
        DiscoverScreen(
            state = DiscoverState(
                recipePreviews = listOf(
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
            onAction = {},
            searchText = ""
        )
    }
}
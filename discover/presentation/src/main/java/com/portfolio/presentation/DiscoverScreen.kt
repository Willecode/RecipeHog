package com.portfolio.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.components.HogFetchingContentBox
import com.portfolio.core.presentation.ui.ObserveAsEvents

@Composable
fun DiscoverScreenRoot(
    viewModel: DiscoverViewModel = org.koin.androidx.compose.koinViewModel(),
    onRecipeClicked: (String) -> Unit
) {

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = viewModel.eventFlow) {event ->
        eventHandler(
            event = event,
            keyboardController = keyboardController,
            context = context
        )
    }

    val searchText by viewModel.searchText.collectAsState()
    val recipePreviews by viewModel.recipePreviews.collectAsState(listOf())
    DiscoverScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        searchText = searchText,
        recipePreviews = recipePreviews,
        onRecipeClicked
    )
}

private fun eventHandler(
    event: DiscoverEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context
) {
    when (event) {
        is DiscoverEvent.Error -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

@Composable
private fun DiscoverScreen(
    state: DiscoverState,
    onAction: (DiscoverAction) -> Unit,
    searchText: String,
    recipePreviews: List<RecipePreview>,
    onRecipeClicked: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val shouldLoadMore: Boolean by remember(
        recipePreviews,
        listState,
        state.throttlingGateOpen,
        state.reachedEndOfData
    ) {
        derivedStateOf {
            val totalItemsCount = recipePreviews.size
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            (lastVisibleItemIndex >= (totalItemsCount - 1)) && state.throttlingGateOpen && recipePreviews.isNotEmpty() && !state.reachedEndOfData
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onAction(DiscoverAction.OnRecipeListEndReached)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {

        TextField(
            value = searchText,
            onValueChange = { onAction(DiscoverAction.OnSearchTextChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Search") },
            enabled = state.searchEnabled
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(recipePreviews) { recipePreview ->
                RecipePreviewListItem(
                    recipePreview = recipePreview,
                    modifier = Modifier.fillParentMaxWidth(),
                    onRecipeClicked
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                    ,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (state.loadingMoreRecipes)
                        CircularProgressIndicator()
                }
            }

        }
    }
    if (state.loading) {
        HogFetchingContentBox()
    }
}

@Composable
private fun RecipePreviewListItem(
    recipePreview: RecipePreview,
    modifier: Modifier = Modifier,
    onRecipeClicked: (String) -> Unit
) {
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
        },
        modifier = modifier.clickable { onRecipeClicked(recipePreview.recipeId) }
    )
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun DiscoverScreenPreview() {
    RecipeHogTheme {
        DiscoverScreen(
            state = DiscoverState(),
            onAction = {},
            searchText = "",
            recipePreviews = mockRecipePreviews(),
            onRecipeClicked = {}
        )
    }
}
@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun DiscoverScreenPreviewLoading() {
    RecipeHogTheme {
        DiscoverScreen(
            state = DiscoverState(loadingMoreRecipes = true),
            onAction = {},
            searchText = "",
            recipePreviews = mockRecipePreviews(),
            onRecipeClicked = {}
        )
    }
}

private fun mockRecipePreviews(): List<RecipePreview> {
    return listOf(
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
}
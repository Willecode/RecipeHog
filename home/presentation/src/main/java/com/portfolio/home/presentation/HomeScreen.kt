package com.portfolio.home.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import com.portfolio.core.domain.Recipe
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.ui.ObserveAsEvents

@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = org.koin.androidx.compose.koinViewModel()
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

    HomeScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

private fun eventHandler(
    event: HomeEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context
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
    }
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val listState = rememberLazyListState()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Recipes:")
        LazyColumn (
            state = listState,
            modifier = Modifier.fillMaxSize()
        ){
            items(state.recipes) { recipe ->
                RecipeItem(
                    title = recipe.title,
                    description = recipe.description
                )
            }
        }
    }


}

@Composable
fun RecipeItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title)
        Text(text = description)
    }

}

@Preview
@Composable
private fun HomeScreenPreview() {
    RecipeHogTheme {
        HomeScreen(
            state = HomeState(
                recipes = listOf(
                    Recipe("Cheese", "This is how you make cheese"),
                    Recipe("Pasta", "This is how you make pasta")
                )
            ),
            onAction = {}
        )
    }
}
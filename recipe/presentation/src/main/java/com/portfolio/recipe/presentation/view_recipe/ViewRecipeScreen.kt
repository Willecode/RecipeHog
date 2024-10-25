@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.portfolio.recipe.presentation.view_recipe

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.portfolio.core.domain.model.IngredientListing
import com.portfolio.core.domain.model.Recipe
import com.portfolio.core.presentation.designsystem.ArrowRightIcon
import com.portfolio.core.presentation.designsystem.BackIcon
import com.portfolio.core.presentation.designsystem.BookmarkAddedIcon
import com.portfolio.core.presentation.designsystem.BookmarkIcon
import com.portfolio.core.presentation.designsystem.ClockIcon
import com.portfolio.core.presentation.designsystem.HeartIcon
import com.portfolio.core.presentation.designsystem.HeartIconFilled
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.components.HogProgressIndicatorBox
import com.portfolio.core.presentation.ui.ObserveAsEvents
import com.portfolio.core.presentation.ui.formatToUi
import com.portfolio.recipe.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable

fun ViewRecipeScreenRoot(
    viewModel: ViewRecipeViewModel = koinViewModel(),
    onBackPress: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onAuthError: () -> Unit,
    onReviewsClicked: (String) -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = viewModel.eventFlow) {event ->
        eventHandler(
            event = event,
            keyboardController = keyboardController,
            context = context,
            onAuthError = onAuthError,
            onReviewsClicked = onReviewsClicked
        )
    }

    ViewRecipeScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                ViewRecipeAction.OnLikeClicked -> Unit
                ViewRecipeAction.OnBookmarkClicked -> Unit
                ViewRecipeAction.OnBackPress -> onBackPress()
                is ViewRecipeAction.OnAuthorClicked -> onAuthorClick(action.authorId)
                ViewRecipeAction.OnReviewsClicked -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

private fun eventHandler(
    event: ViewRecipeEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onAuthError: () -> Unit,
    onReviewsClicked: (String) -> Unit
) {
    when (event) {
        is ViewRecipeEvent.ViewRecipeError -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }

        ViewRecipeEvent.AuthError -> {onAuthError()}
        is ViewRecipeEvent.OnReviewsClicked -> onReviewsClicked(event.recipeId)
    }
}

@Composable
private fun ViewRecipeScreen(
    state: ViewRecipeState,
    onAction: (ViewRecipeAction) -> Unit
) {

    if (state.recipe == null) {
        if (state.cantGetRecipe) {
            RecipeNotFoundBox()
        } else if (state.isLoading) {
            HogProgressIndicatorBox()
        }
    } else {
        RecipeSheetScaffold(
            recipe = state.recipe,
            onAction = onAction,
            isLiked = state.isRecipeLiked,
            isBookmarked = state.isRecipeBookmarked,
            isLikeBookmarkEnabled = state.likeBookmarkAvailable
        )
    }
    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = { onAction(ViewRecipeAction.OnBackPress) },
            colors = IconButtonDefaults.iconButtonColors().copy(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Icon(imageVector = BackIcon, contentDescription = "Go back")
        }
    }

}

@Composable
private fun RecipeSheetScaffold(
    recipe: Recipe,
    onAction: (ViewRecipeAction) -> Unit,
    isLiked: Boolean,
    isBookmarked: Boolean,
    isLikeBookmarkEnabled: Boolean
) {

    val sheetPeekHeight = ((LocalConfiguration.current.screenHeightDp * (2f/3f))).dp
    BottomSheetScaffold(
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Column {
                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "by ${recipe.author}",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.clickable {
                                onAction(ViewRecipeAction.OnAuthorClicked(recipe.authorUserId))
                            }
                        )
                        Row(
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            IconAndText(
                                icon = ClockIcon,
                                text = "${recipe.durationMinutes} min"
                            )
                            IconAndText(
                                icon = HeartIcon,
                                text = recipe.likeCount.toString()
                            )
                        }
                    }
                    Row {
                        IconButton(
                            onClick = { onAction(ViewRecipeAction.OnLikeClicked) },
                            enabled = isLikeBookmarkEnabled
                        ) {
                            Icon(
                                imageVector = if (isLiked) HeartIconFilled else HeartIcon,
                                tint = if (isLiked) Color.Red else LocalContentColor.current,
                                contentDescription = stringResource(id = if (isLiked) R.string.unlike_recipe else R.string.like_recipe)
                            )
                        }
                        IconButton(
                            onClick = { onAction(ViewRecipeAction.OnBookmarkClicked) },
                            enabled = isLikeBookmarkEnabled
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) BookmarkAddedIcon else BookmarkIcon,
                                contentDescription = stringResource(id = if (isBookmarked) R.string.remove_bookmark else R.string.bookmark_recipe)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Description", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = recipe.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                ListItem(
                    headlineContent = { Text(text = stringResource(id = R.string.reviews)) },
                    trailingContent = { Icon(imageVector = ArrowRightIcon, contentDescription = null) },
                    modifier = Modifier.clickable {
                        onAction(ViewRecipeAction.OnReviewsClicked)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Tags", style = MaterialTheme.typography.titleMedium)
                TagGrid(tags = recipe.tags, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                var showIngredients by remember { mutableStateOf(true) }
                RecipeViewSwitch(
                    onIngredientsClicked = { showIngredients = true },
                    onPreparationClicked = { showIngredients = false },
                    showIngredients = showIngredients
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (showIngredients) {
                    IngredientsList(
                        ingredients = recipe.ingredients
                    )
                } else {
                    PreparationSteps(
                        steps = recipe.instructions
                    )
                }
            }
        },
        sheetMaxWidth = Dp.Unspecified,
        sheetPeekHeight = sheetPeekHeight
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .weight(1f)
            ) {
                AsyncImage(
                    model = recipe.imgUrl,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(Modifier.height(sheetPeekHeight - 25.dp))
        }
    }
}

@Composable
private fun RecipeNotFoundBox() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.recipe_not_found),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PreparationSteps(
    modifier: Modifier = Modifier,
    steps: List<String>
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        steps.forEachIndexed(){i, step ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${i + 1}",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun IngredientsList(
    modifier: Modifier = Modifier,
    ingredients : List<IngredientListing>
) {
    Column(
        modifier = modifier,
        verticalArrangement =  Arrangement.spacedBy(8.dp)
    ) {
        ingredients.forEach{listing ->
            Row(

            ) {
                Text(
                    text = "${if(listing.quantity != null) listing.quantity!!.formatToUi() else ""} ${listing.unit}",
                    modifier = Modifier.width(100.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = listing.name)
            }
        }
    }
}

@Composable
private fun RecipeViewSwitch(
    modifier: Modifier = Modifier,
    onIngredientsClicked : () -> Unit,
    onPreparationClicked: () -> Unit,
    showIngredients: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIngredients) {
            Button(onClick = {}) {
                Text(stringResource(id = R.string.ingredients))
            }
        } else {
            OutlinedButton(onClick = onIngredientsClicked) {
                Text(stringResource(id = R.string.ingredients))
            }
        }
        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp))
        if (!showIngredients) {
            Button(onClick = {}) {
                Text(stringResource(id = R.string.preparation))
            }
        } else {
            OutlinedButton(onClick = onPreparationClicked) {
                Text(stringResource(id = R.string.preparation))
            }
        }
    }
}

@Composable
private fun TagGrid(
    modifier: Modifier = Modifier,
    tags: List<String>
) {
    if (tags.isEmpty())
        Text(text = "-")
    else {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tags.forEach{tag ->
                SuggestionChip(
                    onClick = { /*TODO*/ },
                    label = {
                        Text(text = tag)
                    }
                )
            }
        }
    }
}

@Composable
private fun IconAndText(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription ="" )
        Text(text = text, style = MaterialTheme.typography.titleSmall)
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ViewRecipeScreenPreview() {
    val recipeState = ViewRecipeState(
        recipe = Recipe(
            author = "Chef Jamie",
            authorUserId = "jamie123",
            title = "Spaghetti Carbonara",
            description = "A classic Italian pasta dish made with eggs, cheese, pancetta, and pepper. Simple yet delicious!",
            durationMinutes = 30,
            likeCount = 125,
            imgUrl = "https://example.com/images/carbonara.jpg",
            servings = 4,
            tags = listOf("Italian", "Pasta", "Dinner", "Quick", "Favorite", "Easy"),
            instructions = listOf(
                "Boil a large pot of salted water and cook the spaghetti until al dente.",
                "While the pasta is cooking, fry the pancetta until crisp.",
                "In a bowl, whisk together eggs, Parmesan cheese, and black pepper.",
                "Drain the pasta, reserving some of the pasta water.",
                "Quickly toss the hot pasta with the pancetta and the egg mixture, adding pasta water as needed to create a creamy sauce.",
                "Serve immediately, garnished with extra cheese and black pepper."
            ),
            ingredients = listOf(
                IngredientListing(name = "Spaghetti", quantity = 400f, unit = "g"),
                IngredientListing(name = "Pancetta", quantity = 150f, unit = "g"),
                IngredientListing(name = "Eggs", quantity = 3f, unit = "pcs"),
                IngredientListing(name = "Parmesan Cheese", quantity = 100f, unit = "g"),
                IngredientListing(name = "Black Pepper", quantity = 1f, unit = "tsp"),
                IngredientListing(name = "Salt", quantity = 0f, unit = "to taste")
            )
        )
    )

    RecipeHogTheme {
        ViewRecipeScreen(
            state = recipeState,
            onAction = {}
        )
    }
}
package com.portfolio.profile.presentation.view_profile

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.portfolio.core.domain.model.PublicUserData
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.components.RecipeCarousel
import com.portfolio.core.presentation.ui.ObserveAsEvents
import com.portfolio.profile.presentation.R
import com.portfolio.profile.presentation.components.ProfileBadge
import com.portfolio.profile.presentation.components.ProfileImage
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable

fun ViewProfileScreenRoot(
    viewModel: ViewProfileViewModel = koinViewModel(),
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

    ViewProfileScreen(
        state = viewModel.state,
        onAction = {action ->
            when (action) {
                is ViewProfileAction.OnRecipeClick -> onRecipeClick(action.recipeId)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

private fun eventHandler(
    event: ViewProfileEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onAuthError: () -> Unit
) {
    when (event) {
        is ViewProfileEvent.ViewProfileError -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }
        ViewProfileEvent.AuthError -> {
            onAuthError()
        }
    }
}

@Composable
fun ViewProfileScreen(
    state: ViewProfileState,
    onAction: (ViewProfileAction) -> Unit,
    additionalContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {
            ProfileImage(
                pictureUrl = state.publicUserData.profilePictureUrl,
                modifier = Modifier.padding(16.dp).width(200.dp)
            )
            Text(text = state.publicUserData.displayName, style = MaterialTheme.typography.displaySmall)
        }
        ProfileBadge(
            likes = state.publicUserData.likes.toString(),
            creationDate = state.publicUserData.creationDate.toString()
        )
        Spacer(modifier = Modifier.height(32.dp))
        RecipeCarousel(
            title = stringResource(id = R.string.posted_recipes),
            items = state.publicUserData.postedRecipes,
            onRecipeClick = {recipeId -> onAction(ViewProfileAction.OnRecipeClick(recipeId))}
        )
        additionalContent()
    }
}



@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ViewProfileScreenPreview() {
    RecipeHogTheme {
        Surface {
            ViewProfileScreen(
                state = ViewProfileState(
                    publicUserData = PublicUserData(
                        displayName = "User's Name",
                        creationDate = LocalDate.now(),
                        likes = 12,
                        profilePictureUrl = "",
                        postedRecipes = listOf(
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
                    )
                ),
                onAction = {}
            )
        }
    }
}
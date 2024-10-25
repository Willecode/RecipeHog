package com.portfolio.review.presentation

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.SendIcon
import com.portfolio.core.presentation.designsystem.StarIcon
import com.portfolio.core.presentation.designsystem.StarOutlineIcon
import com.portfolio.core.presentation.ui.ObserveAsEvents
import com.portfolio.review.domain.Review

@Composable
fun ReviewScreenRoot(
    viewModel: ReviewViewModel = org.koin.androidx.compose.koinViewModel(),
    onUserClick: (String) -> Unit,
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


    val reviews = viewModel.reviews.collectAsStateWithLifecycle(initialValue = listOf())
    ReviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                is ReviewAction.OnAuthorClick -> {
                    onUserClick(action.authorId)
                }
                else -> Unit
            }
            viewModel.onAction(action)
        },
        reviews = reviews.value
    )
}

private fun eventHandler(
    event: ReviewEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onAuthError: () -> Unit
) {
    when (event) {
        is ReviewEvent.ReviewError -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }

        ReviewEvent.AuthError -> { onAuthError() }
        ReviewEvent.ReviewPostedSuccessfully -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                context.getString(R.string.review_posted),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

@Composable
private fun ReviewScreen(
    state: ReviewState,
    reviews: List<Review>,
    onAction: (ReviewAction) -> Unit
) {
    Column(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Box(Modifier.weight(1f)) {
            LazyColumn {
                items(reviews) { review ->
                    ReviewItem(
                        review = review,
                        onAuthorClick = { onAction(ReviewAction.OnAuthorClick(it)) }
                    )
                }
            }
        }
        Box {
            CommentField(
                onPostComment = { body, rating ->
                    onAction(ReviewAction.OnPostReview(body = body, rating = rating))
                },
                modifier = Modifier.fillMaxWidth(),
                state = state
            )
        }
    }
}

@Composable
fun CommentField(
    onPostComment: (String, Int) -> Unit,
    state: ReviewState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        var rating by rememberSaveable {
            mutableIntStateOf(1)
        }
        ReviewStarsInteractable(
            onSetRating = { rating = it },
            rating = rating
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = modifier
        ) {
            var body by rememberSaveable {
                mutableStateOf("")
            }

            TextField(
                value = body,
                onValueChange = {
                    if (it.length < 100)
                        body = it
                },
                placeholder = { Text(text = "${stringResource(id = R.string.write_review)}...") },
                modifier = Modifier.weight(1f)
            )
            if (state.postingReview) {
                CircularProgressIndicator()
            } else {
                IconButton(onClick = { onPostComment(body, rating) }) {
                    Icon(
                        imageVector = SendIcon,
                        contentDescription = stringResource(id = R.string.cd_send),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

            }

        }
    }
}

@Composable
fun ReviewStarsInteractable(
    onSetRating: (Int) -> Unit,
    rating: Int,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0f))
    ) {
        for (i in 1..5) {
            IconButton(onClick = { onSetRating(i) }) {
                Icon(
                    imageVector = if (i <= rating) StarIcon else StarOutlineIcon,
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review, onAuthorClick: (String) -> Unit) {
    ListItem(
        headlineContent = { Text(review.author) },
        overlineContent = {
            ReviewStars(review.stars)
        },
        supportingContent = {
            Text(text = review.body)
        },
        modifier = Modifier.clickable {
            onAuthorClick(review.authorUserId)
        }
    )
}

@Composable
fun ReviewStars(stars: Int) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= stars) StarIcon else StarOutlineIcon,
                contentDescription = null,
                tint = Color.Yellow
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReviewScreenPreview() {
    RecipeHogTheme {
        ReviewScreen(
            state = ReviewState(),
            onAction = {},
            reviews = listOf(
                Review(
                    author = "Jane Doe",
                    authorUserId = "user123",
                    stars = 5,
                    body = "These cookies turned out amazing! Crispy on the edges, soft in the middle, and perfectly sweet. Will definitely make these again!"
                ),
                Review(
                    author = "John Smith",
                    authorUserId = "user456",
                    stars = 4,
                    body = "Great recipe! The cookies were delicious, though I added a bit more vanilla for extra flavor. Kids loved them!"
                ),
                Review(
                    author = "Emily Chang",
                    authorUserId = "user789",
                    stars = 3,
                    body = "Good, but a bit too sweet for my taste. Next time, I'll reduce the sugar slightly. Otherwise, the texture was spot-on!"
                ),
                Review(
                    author = "Carlos R.",
                    authorUserId = "user101",
                    stars = 5,
                    body = "Best cookie recipe I’ve ever tried! I brought these to a party, and everyone was asking for the recipe. Perfectly chewy and so tasty."
                ),
                Review(
                    author = "Sofia Lopez",
                    authorUserId = "user202",
                    stars = 4,
                    body = "Loved these! I added chocolate chunks along with chips, and it took them to the next level. Highly recommend this recipe."
                ),
                Review(
                    author = "Michael Brown",
                    authorUserId = "user303",
                    stars = 2,
                    body = "I followed the recipe exactly, but they came out a bit flat. Maybe I did something wrong, but they didn't look like the photo."
                ),
                Review(
                    author = "Lily Thompson",
                    authorUserId = "user404",
                    stars = 5,
                    body = "This is now my go-to cookie recipe! Super easy to follow, and the results are incredible every time. Perfect balance of flavors."
                ),
                Review(
                    author = "David Kim",
                    authorUserId = "user505",
                    stars = 3,
                    body = "The cookies were okay, but I found them a bit dry. Next time, I might try reducing the baking time for a softer texture."
                ),
                Review(
                    author = "Alice Nguyen",
                    authorUserId = "user606",
                    stars = 4,
                    body = "Great recipe! I added a pinch of sea salt on top before baking, and it gave the cookies an amazing flavor boost."
                ),
                Review(
                    author = "Tom White",
                    authorUserId = "user707",
                    stars = 5,
                    body = "Absolutely perfect! These cookies have the best texture and taste, and they were gone within minutes. Will make a double batch next time."
                )
            )
        )
    }
}
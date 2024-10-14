package com.portfolio.core.presentation.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.presentation.designsystem.R
import com.portfolio.core.presentation.designsystem.RecipeHogTheme

@Composable
fun RecipeHighlightCard(
    title: String,
    recipe: RecipePreview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        onClick = onClick

    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
        )
        HorizontalDivider(Modifier.height(2.dp))
        AsyncImage(
            model = recipe.imgUrl,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.sample_food_pic),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Column(
            modifier.
            padding(vertical = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = recipe.title, style = MaterialTheme.typography.titleSmall)
            Text(text = "by ${recipe.author}", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = recipe.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RecipeHighlightCardPreview() {
    RecipeHogTheme {
        RecipeHighlightCard(
            title = "Recipe of the day",
            recipe = RecipePreview(
                title = "Spaghetti Carbonara",
                description = "A classic Italian pasta dish made with eggs, cheese, pancetta, and pepper. Simple yet rich in flavor, this dish is perfect for a quick and comforting dinner.",
                author = "John Doe",
                imgUrl = "https://i.imgur.com/k38gYtr.jpeg",
                recipeId = ""
            ),
            onClick = {}
        )
    }
}
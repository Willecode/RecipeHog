package com.portfolio.core.presentation.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.presentation.designsystem.RecipeHogTheme

@Composable
fun RecipeCarousel(
    title: String,
    items: List<RecipePreview>,
    onRecipeClick: (String) -> Unit
) {

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) {item ->
                Column(
                    modifier = Modifier.clickable { onRecipeClick(item.recipeId) }
                ) {
                    AsyncImage(
                        model = item.imgUrl,
                        contentDescription = "",
                        modifier = Modifier
                            .width(130.dp)
                            .height(130.dp)
                            .clip(RoundedCornerShape(20)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.width(130.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "by ${item.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.width(130.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1

                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RecipeCarouselPreview() {
    RecipeHogTheme {
        Surface {
            RecipeCarousel(
                title = "Favorites",
                onRecipeClick = {},
                items = listOf(
                    RecipePreview(
                        title = "Title",
                        author = "Author",
                        description = "",
                        imgUrl = "",
                        recipeId = ""
                    ),
                    RecipePreview(
                        title = "Super long title that just goes on",
                        author = "Author",
                        description = "",
                        imgUrl = "",
                        recipeId = ""
                    ),
                    RecipePreview(
                        title = "Title",
                        author = "Author",
                        description = "",
                        imgUrl = "",
                        recipeId = ""
                    )
                )
            )
        }
    }
}
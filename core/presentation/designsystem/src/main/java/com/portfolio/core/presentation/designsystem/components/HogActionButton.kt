package com.portfolio.core.presentation.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.portfolio.core.presentation.designsystem.R
import com.portfolio.core.presentation.designsystem.RecipeHogTheme

@Composable
fun HogIntroActionButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            Color.Transparent
        ),
        contentPadding = PaddingValues(),
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (enabled) {
                        Modifier.background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    colorResource(id = R.color.intro_primary),
                                    colorResource(id = R.color.intro_variant)
                                )
                            )
                        )
                    }
                    else {
                        Modifier
                    }
                )
                .padding(0.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(if (isLoading) 1f else 0f),
                strokeWidth = 1.5.dp,
                color = Color.White
            )
            Text(
                text = text,
                modifier = Modifier
                    .alpha(if(isLoading) 0f else 1f),
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) Color.White else Color.Gray.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun HogOutlinedActionButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onBackground,

    ) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color,
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = color
        ),
        shape = RoundedCornerShape(100f),
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(if (isLoading) 1f else 0f),
                strokeWidth = 1.5.dp,
                color = color
            )
            Text(
                text = text,
                modifier = Modifier
                    .alpha(if(isLoading) 0f else 1f),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
private fun HogActionButtonPreview(){
    RecipeHogTheme {
        HogIntroActionButton(text = "Test", isLoading = false) {

        }
    }
}

@Preview
@Composable
private fun HogActionButtonDisabledPreview(){
    RecipeHogTheme {
        HogIntroActionButton(text = "Test", isLoading = false, enabled = false) {

        }
    }
}

@Preview
@Composable
private fun HogActionButtonLoadingPreview(){
    RecipeHogTheme {
        HogIntroActionButton(text = "Test", isLoading = true, enabled = false) {

        }
    }
}
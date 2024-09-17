package com.portfolio.core.presentation.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.portfolio.core.presentation.designsystem.R
import com.portfolio.core.presentation.designsystem.RecipeHogTheme

@Composable
fun HogLogo(
    modifier: Modifier = Modifier,
    shadow: Boolean = false,
    size: TextUnit = 12.sp,
    color: Color = colorResource(id = R.color.intro_logo)
) {
    Text(
        text = "RecipeHog",
        fontFamily = FontFamily(
            Font(
                resId = R.font.bevan
            )
        ),
        color = color,
        style = TextStyle(
            fontSize = size,
            shadow = if (shadow) Shadow(
                offset = Offset(5f, 10f),
                blurRadius = 3f
            ) else null
        )
    )
}

@Preview
@Composable
private fun HogLogoPreview(){
    RecipeHogTheme {
        HogLogo()
    }
}
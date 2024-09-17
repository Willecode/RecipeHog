package com.portfolio.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.portfolio.core.presentation.designsystem.R

@Composable
fun BlurredImageBackground(
    modifier: Modifier = Modifier,
    image: Painter,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(radius = 8.dp)
            .paint(
                image,
                contentScale = ContentScale.Crop
            )
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        content()
    }
}
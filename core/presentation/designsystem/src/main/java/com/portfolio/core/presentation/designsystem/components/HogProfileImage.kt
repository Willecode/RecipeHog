package com.portfolio.core.presentation.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.portfolio.core.presentation.designsystem.R


@Composable
fun HogProfileImage(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(id = R.drawable.outline_person_24)
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(100))
            .background(Color.Gray)
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(id = R.string.cd_profile_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
private fun HogProfileImagePreview(){
    HogProfileImage(
        painter = painterResource(id = R.drawable.outline_person_24),
        modifier = Modifier.width(100.dp)
    )
}
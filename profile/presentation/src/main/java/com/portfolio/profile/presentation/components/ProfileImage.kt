package com.portfolio.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.portfolio.core.presentation.designsystem.PersonIcon

@Composable
fun ProfileImage(
    pictureUrl: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .aspectRatio(1f)
    ){
        if (pictureUrl.isBlank()) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = PersonIcon,
                    contentDescription = "",
                    modifier = Modifier.size(100.dp)
                )
            }
        } else {
            AsyncImage(
                model = pictureUrl,
                contentDescription = "",
                modifier = Modifier
//                    .width(200.dp)
//                    .height(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}
package com.portfolio.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.portfolio.core.presentation.designsystem.HeartIconFilled
import com.portfolio.profile.presentation.R

@Composable
fun ProfileBadge(likes: String, creationDate: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Column {
            Text(text = stringResource(id = R.string.recipe_likes))
            Row {
                Icon(
                    imageVector = HeartIconFilled,
                    tint = Color.Red,
                    contentDescription = ""
                )
                Text(text = likes)
            }
        }
        Spacer(modifier = Modifier.width(64.dp))
        Column {
            Text(text = stringResource(id = R.string.member_since))
            Text(text = creationDate)
        }
    }
}
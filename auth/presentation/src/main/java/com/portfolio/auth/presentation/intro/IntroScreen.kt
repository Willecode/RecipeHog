package com.portfolio.auth.presentation.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.auth.presentation.R
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.components.HogIntroActionButton
import com.portfolio.core.presentation.designsystem.components.HogLogo
import com.portfolio.core.presentation.designsystem.components.HogOutlinedActionButton

@Composable
fun IntroScreenRoot(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    IntroScreen(
        onAction = {action ->
            when (action) {
                IntroAction.OnLoginClicked -> onLoginClick()
                IntroAction.OnRegisterClicked -> onRegisterClick()
            }
        }
    )
}

@Composable
fun IntroScreen(
    onAction: (IntroAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.pizza_low_res),
                contentScale = ContentScale.Crop
            )
    ) {
        Box(
            modifier = Modifier.background(
//                Brush.verticalGradient(
//                    colors = listOf(
//                        Color.Black.copy(alpha = 0.4f),
//                        Color.Black
//                    )
//                )
                Brush.verticalGradient(
                    0.3f to Color.Black.copy(alpha = 0.3f),
                    0.55f to Color.Black
                )
            )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 48.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    HogLogo(
                        shadow = true,
                        size = 40.sp
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                    ) {
                        Column {
                            Text(
                                text = stringResource(id = R.string.intro_additional),
                                fontSize = 16.sp,
                                color = Color.LightGray.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.intro_title),
                                fontSize = 50.sp,
                                lineHeight = 50.sp,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.intro_body),
                                fontSize = 16.sp,
                                color = Color.LightGray
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        HogIntroActionButton(
                            text = "Login",
                            isLoading = false,
                            onClick = { onAction(IntroAction.OnLoginClicked) },
                            modifier = Modifier.height(60.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HogOutlinedActionButton(
                            text = "Register",
                            isLoading = false,
                            onClick = { onAction(IntroAction.OnRegisterClicked) },
                            color = Color.White,
                            modifier = Modifier.height(60.dp)
                        )
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun IntroScreenPreview() {
    RecipeHogTheme {
        IntroScreen {}
    }
}
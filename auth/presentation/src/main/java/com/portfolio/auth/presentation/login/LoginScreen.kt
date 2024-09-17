package com.portfolio.auth.presentation.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.auth.presentation.R
import com.portfolio.core.presentation.designsystem.EmailIcon
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.Roboto
import com.portfolio.core.presentation.designsystem.components.BlurredImageBackground
import com.portfolio.core.presentation.designsystem.components.HogIntroActionButton
import com.portfolio.core.presentation.designsystem.components.HogPasswordTextField
import com.portfolio.core.presentation.designsystem.components.HogIntroTextField
import com.portfolio.core.presentation.designsystem.components.HogLogo
import com.portfolio.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    onRegisterClicked: () -> Unit,
    onSuccessfulLogin: () -> Unit,
    viewModel: LoginViewModel= koinViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = viewModel.eventFlow) {event ->
        eventHandler(
            event = event,
            keyboardController = keyboardController,
            context = context,
            onSuccessfulLogin = onSuccessfulLogin
        )
    }

    LoginScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                LoginAction.OnRegisterClicked -> {
                    onRegisterClicked()
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

private fun eventHandler(
    event: LoginEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onSuccessfulLogin: () -> Unit
) {
    when (event) {
        is LoginEvent.LoginError -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }
        is LoginEvent.LoginSuccess -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                R.string.login_succssful,
                Toast.LENGTH_LONG
            ).show()
            onSuccessfulLogin()
        }
    }
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    BlurredImageBackground(image = painterResource(id = R.drawable.soup)) {}
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Login", fontSize = 40.sp, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            HogIntroTextField(
                state = state.email,
                startIcon = EmailIcon,
                endIcon = null,
                hint = "example@email.com",
                title = "Email"
            )
            HogPasswordTextField(
                state = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = { onAction(LoginAction.OnPasswordVisibilityChanged(!state.isPasswordVisible)) },
                hint = "",
                title = "Password"
            )

            Spacer(modifier = Modifier.height(12.dp))
            HogIntroActionButton(
                text = "Login",
                isLoading = state.isLoggingIn,
                onClick = { onAction(LoginAction.OnLoginClicked) },
                modifier = Modifier.height(60.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            RegisterClickableText(
                onTextClick = { onAction(LoginAction.OnRegisterClicked) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            HogLogo(size = 20.sp, shadow = true)
        }
    }
}

@Composable
private fun RegisterClickableText(onTextClick: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontFamily = Roboto,
                color = Color.White
            )
        ) {
            append(stringResource(id = R.string.dont_have_an_account) + " ")
            pushStringAnnotation(
                tag = "clickable_text",
                annotation = stringResource(id = R.string.register)
            )
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = Roboto
                )
            ) {
                append(stringResource(id = R.string.register))
            }
        }
    }
    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "clickable_text",
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                onTextClick()
            }
        }
    )
}

@Preview
@Composable
private fun LoginScreenPreview() {
    RecipeHogTheme {
        LoginScreen(
            state = LoginState(
                email = TextFieldState(),
                password = TextFieldState(),
                isPasswordVisible = false,
                isLoggingIn = false,
                canLogin = true
            ),
            onAction = {}
        )
    }

}



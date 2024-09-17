package com.portfolio.auth.presentation.register

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.auth.domain.PasswordValidationState
import com.portfolio.auth.domain.RegisterCredentialValidator
import com.portfolio.auth.presentation.R
import com.portfolio.core.presentation.designsystem.CheckIcon
import com.portfolio.core.presentation.designsystem.CrossIcon
import com.portfolio.core.presentation.designsystem.EmailIcon
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.Roboto
import com.portfolio.core.presentation.designsystem.components.BlurredImageBackground
import com.portfolio.core.presentation.designsystem.components.HogIntroActionButton
import com.portfolio.core.presentation.designsystem.components.HogIntroTextField
import com.portfolio.core.presentation.designsystem.components.HogLogo
import com.portfolio.core.presentation.designsystem.components.HogPasswordTextField
import com.portfolio.core.presentation.ui.ObserveAsEvents
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable

fun RegisterScreenRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onLoginClicked: () -> Unit,
    onSuccessfulRegister: () -> Unit
) {

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = viewModel.eventFlow) {event ->
        eventHandler(
            event = event,
            keyboardController = keyboardController,
            context = context,
            onSuccessfulRegister = onSuccessfulRegister
        )
    }

    LaunchedEffect(true) {
        launch {
            viewModel.observeAndValidateEmail()
        }
        launch {
            viewModel.observeAndValidatePassword()
        }
    }
    RegisterScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                RegisterAction.OnLoginClicked -> {
                    onLoginClicked()
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

private fun eventHandler(
    event: RegisterEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onSuccessfulRegister: () -> Unit
) {
    when (event) {
        is RegisterEvent.RegisterError -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }
        is RegisterEvent.RegisterSuccess -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                R.string.register_successful,
                Toast.LENGTH_LONG
            ).show()
            onSuccessfulRegister()
        }
    }
}

@Composable
private fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
) {
    BlurredImageBackground(image = painterResource(id = R.drawable.food_bowls)) {}
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
            Text(
                text = stringResource(id = R.string.register),
                fontSize = 40.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            HogIntroTextField(
                state = state.email,
                startIcon = EmailIcon,
                endIcon = null,
                hint = "example@email.com",
                title = "Email",
                error = if (state.isEmailValid) null else stringResource(id = R.string.must_be_valid_email)

            )
            HogPasswordTextField(
                state = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = { onAction(RegisterAction.OnPasswordVisibilityChanged) },
                hint = "",
                title = "Password"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PasswordRequirements(
                modifier = Modifier.fillMaxWidth(),
                passwordValidationState = state.passwordValidationState
            )
            Spacer(modifier = Modifier.height(12.dp))
            HogIntroActionButton(
                text = stringResource(id = R.string.register),
                isLoading = state.isRegistering,
                onClick = { onAction(RegisterAction.OnRegisterClicked) },
                modifier = Modifier.height(60.dp),
                enabled = (state.isEmailValid && state.passwordValidationState.isValidPassword && (!state.isRegistering))
            )
            Spacer(modifier = Modifier.height(12.dp))
            LoginClickableText(
                onTextClick = { onAction(RegisterAction.OnLoginClicked) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            HogLogo(size = 20.sp, shadow = true)
        }
    }
}

@Composable
private fun PasswordRequirements(
    modifier: Modifier = Modifier,
    passwordValidationState: PasswordValidationState
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.password_must) + ":",
            color = colorResource(
                id = com.portfolio.core.presentation.designsystem.R.color.intro_textfield_unfocus
            )
        )
        Column(
            modifier = Modifier.padding(start = 30.dp)
        ) {
            PasswordRequirement(
                text = stringResource(
                    id = R.string.contain_at_least_x_characters,
                    RegisterCredentialValidator.PASSWORD_MIN_LENGTH
                ),
                isValid = passwordValidationState.hasMinLength
            )
            PasswordRequirement(
                text = stringResource(id = R.string.contain_number),
                isValid = passwordValidationState.hasNumber
            )
            PasswordRequirement(
                text = stringResource(id = R.string.contain_lowercase_letter),
                isValid = passwordValidationState.hasLowerCaseCharacter
            )
            PasswordRequirement(
                text = stringResource(id = R.string.contain_uppercase_letter),
                isValid = passwordValidationState.hasUpperCaseCharacter
            )
        }
    }
}

@Composable
fun PasswordRequirement(
    text: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) {
                CheckIcon
            } else {
                CrossIcon
            },
            contentDescription = null,
            tint = if (isValid) {
                colorResource(id = com.portfolio.core.presentation.designsystem.R.color.password_validator_green)
            } else {
                colorResource(id = com.portfolio.core.presentation.designsystem.R.color.password_validator_red)
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = colorResource(id = com.portfolio.core.presentation.designsystem.R.color.intro_textfield_unfocus),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun LoginClickableText(onTextClick: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontFamily = Roboto,
                color = Color.White
            )
        ) {
            append(stringResource(id = R.string.already_have_an_account) + " ")
            pushStringAnnotation(
                tag = "clickable_text",
                annotation = stringResource(id = R.string.login)
            )
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = Roboto
                )
            ) {
                append(stringResource(id = R.string.login))
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
private fun RegisterScreenPreview() {
    RecipeHogTheme {
        RegisterScreen(
            state = RegisterState(),
            onAction = {}
        )
    }
}
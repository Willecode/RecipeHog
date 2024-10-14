package com.portfolio.auth.presentation.userinfo

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.portfolio.auth.presentation.R
import com.portfolio.core.presentation.designsystem.PersonIcon
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.components.BlurredImageBackground
import com.portfolio.core.presentation.designsystem.components.HogIntroActionButton
import com.portfolio.core.presentation.designsystem.components.HogIntroTextField
import com.portfolio.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable

fun UserInfoScreenRoot(
    viewModel: UserInfoViewModel = koinViewModel(),
    onUserInfoUpdateSuccess: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = viewModel.eventFlow) {event ->
        eventHandler(
            event = event,
            keyboardController = keyboardController,
            context = context,
            onUserInfoUpdateSuccess
        )
    }

    UserInfoScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

private fun eventHandler(
    event: UserInfoEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onUserInfoUpdateSuccess: () -> Unit
) {
    when (event) {
        is UserInfoEvent.UserInfoError -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }
        is UserInfoEvent.UserInfoUpdateSuccess -> {
            keyboardController?.hide()
            onUserInfoUpdateSuccess()
        }
    }
}

@Composable
private fun UserInfoScreen(
    state: UserInfoState,
    onAction: (UserInfoAction) -> Unit
) {
    BlurredImageBackground(image = painterResource(id = R.drawable.soup)) {}
    UserNameInputForm(state, onAction)
}

@Composable
fun UserNameInputForm(state: UserInfoState, onAction: (UserInfoAction) -> Unit) {
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
            Text(text = "Username", fontSize = 40.sp, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            HogIntroTextField(
                state = state.username,
                startIcon = PersonIcon,
                endIcon = null,
                hint = "My Name",
                title = "Choose a username"
            )
            Spacer(modifier = Modifier.height(24.dp))
            HogIntroActionButton(
                text = "Save",
                isLoading = state.isLoading,
                onClick = { onAction(UserInfoAction.OnUserNameUpdateClick) },
                modifier = Modifier.height(60.dp)
            )
        }
    }
}

@Preview
@Composable
private fun UserInfoScreenPreview() {
    RecipeHogTheme {
        UserInfoScreen(
            state = UserInfoState(),
            onAction = {}
        )
    }
}
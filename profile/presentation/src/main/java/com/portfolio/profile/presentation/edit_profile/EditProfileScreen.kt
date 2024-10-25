@file:OptIn(ExperimentalMaterial3Api::class)

package com.portfolio.profile.presentation.edit_profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.portfolio.core.presentation.designsystem.ArrowRightIcon
import com.portfolio.core.presentation.designsystem.BackIcon
import com.portfolio.core.presentation.designsystem.CameraIcon
import com.portfolio.core.presentation.designsystem.EditIcon
import com.portfolio.core.presentation.designsystem.GalleryIcon
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.components.CameraPreview
import com.portfolio.core.presentation.designsystem.components.HogCameraPermissionRationaleDialog
import com.portfolio.core.presentation.ui.ObserveAsEvents
import com.portfolio.profile.presentation.R
import com.portfolio.profile.presentation.components.ProfileImage
import com.portfolio.profile.presentation.view_profile.ViewProfileAction
import com.portfolio.profile.presentation.view_profile.ViewProfileScreen
import com.portfolio.profile.presentation.view_profile.ViewProfileState
import org.koin.androidx.compose.koinViewModel

@Composable

fun EditProfileScreenRoot(
    viewModel: EditProfileViewModel = koinViewModel(),
    onRecipeClick: (String) -> Unit,
    onAuthError: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = viewModel.eventFlow) {event ->
        eventHandler(
            event = event,
            keyboardController = keyboardController,
            context = context,
            onAuthError = onAuthError
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { hasCameraPermission ->
        val activity = context as ComponentActivity
        val showCameraPermissionRationale = activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        viewModel.onAction(EditProfileAction.OnShowCameraPermRationaleChanged(showCameraRationale = showCameraPermissionRationale))
        viewModel.onAction(EditProfileAction.OnCameraPermissionChanged(hasCameraPermission = hasCameraPermission))
    }

    EditProfileScreen(
        state = viewModel.state,
        onAction = {action ->
            when (action) {
                is EditProfileAction.OnRecipeClick -> onRecipeClick(action.recipeId)
                EditProfileAction.OnRequestCameraPermission -> {
                    permissionLauncher.requestCameraPermissions(
                        context = context,
                        onPermissionIsGranted = {
                            viewModel.onAction(EditProfileAction.OnCameraPermissionChanged(hasCameraPermission = true))
                        }
                    )
                }
                else -> Unit
            }
            viewModel.onAction(action)
        },
        onCameraClick = {
            permissionLauncher.requestCameraPermissions(
                context = context,
                onPermissionIsGranted = {
                    viewModel.onAction(EditProfileAction.OnCameraPermissionChanged(hasCameraPermission = true))
                }
            )
        }
    )
}

private fun eventHandler(
    event: EditProfileEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onAuthError: () -> Unit
) {
    when (event) {
        is EditProfileEvent.EditProfileError -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }
        EditProfileEvent.AuthError -> {
            onAuthError()
        }
    }
}

@Composable
private fun EditProfileScreen(
    state: EditProfileState,
    onAction: (EditProfileAction) -> Unit,
    onCameraClick: () -> Unit
) {
    var showSettings by rememberSaveable { mutableStateOf(false) }
    ViewProfileScreen(
        state = ViewProfileState(
            publicUserData = state.userData.publicUserData
        ),
        onAction = {viewProfileAction ->
            when (viewProfileAction){
                is ViewProfileAction.OnRecipeClick -> {
                    onAction(EditProfileAction.OnRecipeClick(viewProfileAction.recipeId))
                }
            }
        },
        additionalContent = {
            HorizontalDivider(Modifier.padding(vertical = 16.dp))
            Text(
                text = stringResource(id = R.string.account_settings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSettings = true }
            )
        }
    )
    if (showSettings) {
        AccountSettings(
            onBackClick = {showSettings = false},
            state = state,
            onCameraClick = onCameraClick,
            onAction = onAction
        )
    }
    if (state.showCameraPermissionRationale) {
        HogCameraPermissionRationaleDialog(
            onGrantPermission = {
                onAction(EditProfileAction.OnRequestCameraPermission)
            },
            onDontGrantPermission = {
                onAction(
                    EditProfileAction.OnShowCameraPermRationaleChanged(
                        showCameraRationale = false
                    )
                )
            }
        )
    }
}

@Composable
private fun AccountSettings(
    onBackClick: () -> Unit,
    state: EditProfileState,
    onCameraClick: () -> Unit,
    onAction: (EditProfileAction) -> Unit
) {
    BackHandler {
        onBackClick()
    }
    var showNewPicDialog by rememberSaveable { mutableStateOf(false) }
    var showCameraPreview by rememberSaveable { mutableStateOf(false) }
    AccountSettingsForm(
        onBackClick = onBackClick,
        state = state,
        onProfilePicEditClick = { showNewPicDialog = true }
    )
    if (showNewPicDialog) {
        ProfilePicChangeDialog(
            state = state,
            onAction = onAction,
            onCameraClick = {
                showCameraPreview = true
                showNewPicDialog = false
                onCameraClick()
            },
            onDismissRequest = { showNewPicDialog = false }
        )
    }
    if (state.hasCameraPermission && showCameraPreview) {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(
                onPhotoTaken = { bitmap ->
                    showCameraPreview = false
                    showNewPicDialog = true
                    onAction(EditProfileAction.OnPictureTaken(bitmap))
                },
                onCancel = { showCameraPreview = false },
                onError = {}
            )
        }
    }
}

@Composable
private fun AccountSettingsForm(
    onBackClick: () -> Unit,
    state: EditProfileState,
    onProfilePicEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.account_settings)) },
                    navigationIcon = {
                        IconButton(onClick = { onBackClick() }) {
                            Icon(
                                imageVector = BackIcon,
                                contentDescription = stringResource(id = com.portfolio.core.presentation.designsystem.R.string.navigate_back)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .padding(all = 16.dp)
                    .fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    ProfileImage(
                        pictureUrl = state.userData.publicUserData.profilePictureUrl,
                        modifier = Modifier
                            .padding(16.dp)
                            .width(200.dp)
                    )
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        IconButton(
                            onClick = onProfilePicEditClick,
                            colors = IconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Icon(
                                imageVector = EditIcon,
                                contentDescription = stringResource(id = R.string.cd_change_profile_picture)
                            )
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                SettingsItem(
                    headlineContent = { Text(text = stringResource(id = R.string.display_name)) },
                    supportingContent = { Text(state.userData.publicUserData.displayName) },
                    onClick = {}
                )
                SettingsItem(
                    headlineContent = { Text(text = stringResource(id = R.string.email)) },
                    supportingContent = { Text("") },
                    onClick = {}
                )
                SettingsItem(
                    headlineContent = { Text(text = stringResource(id = R.string.theme)) },
                    onClick = {}
                )

            }
        }
    }
}

@Composable
private fun ProfilePicChangeDialog(
    state: EditProfileState,
    onAction: (EditProfileAction) -> Unit,
    onCameraClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (state.picture != null) {
                    val imageBitmap = remember(state.picture) {
                        state.picture.asImageBitmap()
                    }
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(200.dp)
                            .clip(CircleShape)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Card(
                        onClick = {},
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = GalleryIcon,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        onClick = onCameraClick,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = CameraIcon,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(30.dp)
                        )
                    }

                }
                if (state.picture != null) {
                    val context = LocalContext.current
                    if (state.uploadingProfilePicture)
                        CircularProgressIndicator()
                    else {
                        Button(onClick = {
                            onAction(EditProfileAction.UploadProfilePic(context.filesDir.toString()))
                        }) {
                            Text(text = stringResource(id = R.string.set_as_profile_image))
                        }
                    }

                }
            }
        }
    )
}

@Composable
private fun SettingsItem(
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable() (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        trailingContent = {
            Icon(
                imageVector = ArrowRightIcon,
                contentDescription = null
            )
        },
        modifier = Modifier.clickable {
            onClick()
        }
    )
}

private fun ManagedActivityResultLauncher<String, Boolean>.requestCameraPermissions(
    context: Context,
    onPermissionIsGranted: () -> Unit
) {
    val hasCameraPermission = context.checkSelfPermission(Manifest.permission.CAMERA)
    if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
        onPermissionIsGranted()
    } else {
        // Request a permission
        launch(Manifest.permission.CAMERA)
    }
}

@Preview
@Composable
private fun EditProfileScreenPreview() {
    RecipeHogTheme {
        Surface {
            EditProfileScreen(
                state = EditProfileState(),
                onAction = {},
                {}
            )
        }
    }
}
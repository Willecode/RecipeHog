@file:OptIn(ExperimentalMaterial3Api::class)

package com.portfolio.recipe.presentation.create_recipe

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.portfolio.core.presentation.designsystem.AddIcon
import com.portfolio.core.presentation.designsystem.BackIcon
import com.portfolio.core.presentation.designsystem.CameraIcon
import com.portfolio.core.presentation.designsystem.ClockIcon
import com.portfolio.core.presentation.designsystem.DeleteIcon
import com.portfolio.core.presentation.designsystem.GalleryIcon
import com.portfolio.core.presentation.designsystem.PersonIcon
import com.portfolio.core.presentation.designsystem.RecipeHogTheme
import com.portfolio.core.presentation.designsystem.components.CameraPreview
import com.portfolio.core.presentation.designsystem.components.HogCameraPermissionRationaleDialog
import com.portfolio.core.presentation.ui.ObserveAsEvents
import com.portfolio.recipe.presentation.R
import com.portfolio.recipe.presentation.create_recipe.ingredient.IngredientUI
import com.portfolio.recipe.presentation.create_recipe.ingredient.IngredientUnit
import com.portfolio.recipe.presentation.create_recipe.ingredient.util.toUiText
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateRecipeScreenRoot(
    viewModel: CreateRecipeViewModel = koinViewModel(),
    onSuccessfullyPosted: () -> Unit,
    onAuthError: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = viewModel.eventFlow) {event ->
        eventHandler(
            event = event,
            keyboardController = keyboardController,
            context = context,
            onSuccessfullyPosted = onSuccessfullyPosted,
            onAuthError = onAuthError
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { hasCameraPermission ->
        val activity = context as ComponentActivity
        val showCameraPermissionRationale = activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        viewModel.onAction(CreateRecipeAction.OnShowCameraPermRationaleChanged(showCameraRationale = showCameraPermissionRationale))
        viewModel.onAction(CreateRecipeAction.OnCameraPermissionChanged(hasCameraPermission = hasCameraPermission))
    }

    CreateRecipeScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                CreateRecipeAction.OnRequestCameraPermission -> {
                    permissionLauncher.requestCameraPermissions(
                        context = context,
                        onPermissionIsGranted = {
                            viewModel.onAction(CreateRecipeAction.OnCameraPermissionChanged(hasCameraPermission = true))
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
                    viewModel.onAction(CreateRecipeAction.OnCameraPermissionChanged(hasCameraPermission = true))
                }
            )
        }
    )
}

private fun eventHandler(
    event: CreateRecipeEvent,
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    onSuccessfullyPosted: () -> Unit,
    onAuthError: () -> Unit
) {
    when (event) {
        is CreateRecipeEvent.Error -> {
            keyboardController?.hide()
            Toast.makeText(
                context,
                event.error.asString(context),
                Toast.LENGTH_LONG
            ).show()
        }

        CreateRecipeEvent.RecipePostSuccessful -> onSuccessfullyPosted()
        CreateRecipeEvent.AuthError -> { onAuthError() }
    }
}

@Composable
private fun CreateRecipeScreen(
    state: CreateRecipeState,
    onAction: (CreateRecipeAction) -> Unit,
    onCameraClick: () -> Unit
) {

    var showCameraPreview by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = { CreateRecipeTopBar(onAction) }
    ) { paddingValues ->
        CreateRecipeForm(
            paddingValues = paddingValues,
            state = state,
            onAction = onAction,
            onCameraClick = {
                showCameraPreview = true
                onCameraClick()
            }
        )
    }
    if (state.hasCameraPermission) {
        if (showCameraPreview){
            CameraPreview(
                onPhotoTaken = { bitmap ->
                    showCameraPreview = false
                    onAction(CreateRecipeAction.OnPictureTaken(bitmap))
                },
                onCancel = { showCameraPreview = false },
                onError = {}
            )
        }
    }

}

@Composable
private fun CreateRecipeForm(
    paddingValues: PaddingValues,
    state: CreateRecipeState,
    onAction: (CreateRecipeAction) -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.picture),
                style = MaterialTheme.typography.titleMedium
            )
            ImageChooser(
                onGalleryClick = {},
                onCameraClick = { onCameraClick() },
                picture = state.picture
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.title),
                style = MaterialTheme.typography.titleMedium
            )
            TextField(
                value = state.title,
                onValueChange = { title -> onAction(CreateRecipeAction.OnTitleChanged(title)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                isError = state.showTitleError,
                supportingText = {
                    if (state.showTitleError){
                        Text(text = stringResource(id = R.string.cant_be_empty))
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.description),
                style = MaterialTheme.typography.titleMedium
            )
            TextField(
                value = state.description,
                onValueChange = { desc -> onAction(CreateRecipeAction.OnDescriptionChanged(desc)) },
                modifier = Modifier.fillMaxWidth(),
                isError = state.showDescriptionError,
                supportingText = {
                    if (state.showDescriptionError){
                        Text(text = stringResource(id = R.string.cant_be_empty))
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.duration),
                style = MaterialTheme.typography.titleMedium
            )
            DurationInput(state, onAction)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.servings),
                style = MaterialTheme.typography.titleMedium
            )
            TextField(
                value = state.servings,
                onValueChange = { servings -> onAction(CreateRecipeAction.OnServingsChanged(servings)) },
                maxLines = 1,
                modifier = Modifier.width(150.dp),
                leadingIcon = {
                    Icon(
                        imageVector = PersonIcon,
                        contentDescription = stringResource(id = R.string.servings)
                    )
                },
                isError = state.showServingsError,
                supportingText = {
                    if (state.showServingsError){
                        Text(text = stringResource(id = R.string.cant_be_empty))
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.ingredients),
                style = MaterialTheme.typography.titleMedium
            )
            IngredientDrafts(state, onAction)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Preparation", style = MaterialTheme.typography.titleMedium)
            PreparationDrafts(state, onAction)
            Spacer(modifier = Modifier.height(8.dp))
            //Text(text = "Tags", style = MaterialTheme.typography.titleMedium)
            val context = LocalContext.current
            if (state.posting) {
                CircularProgressIndicator()
            } else {
                Button(onClick = {
                    onAction(CreateRecipeAction.OnPostClick(context.filesDir.toString()))
                }) {
                    Text(text = stringResource(id = R.string.post))
                }
            }

        }
    }
    if (state.showCameraPermissionRationale) {
        HogCameraPermissionRationaleDialog(
            onGrantPermission = {
                onAction(CreateRecipeAction.OnRequestCameraPermission)
            },
            onDontGrantPermission = {
                onAction(
                    CreateRecipeAction.OnShowCameraPermRationaleChanged(
                        showCameraRationale = false
                    )
                )
            }
        )
    }
}

@Composable
private fun CreateRecipeTopBar(onAction: (CreateRecipeAction) -> Unit) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onAction(CreateRecipeAction.OnBackClick) }
                ) {
                    Icon(imageVector = BackIcon, contentDescription = "Go back")
                }
                Text(text = "New Recipe")
            }
        },

        )
}

@Composable
private fun PreparationDrafts(
    state: CreateRecipeState,
    onAction: (CreateRecipeAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        state.preparationSteps.forEachIndexed { index, step ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = step.text,
                    onValueChange = {
                        onAction(
                            CreateRecipeAction.OnPreparationStepChanged(
                                stepIndex = index,
                                value = it
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text(
                            text = "${stringResource(id = R.string.step)} ${index + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    },
                    isError = step.showError,
                    supportingText = {
                        if (step.showError){
                            Text(text = stringResource(id = R.string.cant_be_empty))
                        }
                    }
                )
                IconButton(
                    onClick = { onAction(CreateRecipeAction.OnDeletePreparationStep(index)) },
                    //modifier = Modifier.width(100.dp)
                ) {
                    Icon(
                        imageVector = DeleteIcon,
                        contentDescription = stringResource(id = R.string.cd_delete_preparation_step)
                    )
                }
            }
        }
        AddListingButton { onAction(CreateRecipeAction.OnAddPreparationStep) }
    }
}

@Composable
private fun IngredientDrafts(
    state: CreateRecipeState,
    onAction: (CreateRecipeAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        state.ingredientDrafts.forEachIndexed { draftIndex, ingredientDraft ->
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                IngredientNameField(
                    ingredientDraft = ingredientDraft.ingredient,
                    onAction = onAction,
                    draftIndex = draftIndex,
                    modifier = Modifier.fillMaxWidth(),
                    isError = ingredientDraft.showNameError
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        if (ingredientDraft.ingredient is IngredientUI.QuantityIngredient) {
                            IngredientQuantityField(
                                ingredientDraft.ingredient,
                                onAction,
                                draftIndex,
                                Modifier.width(100.dp),
                                isError = ingredientDraft.showQuantityError
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UnitDropDown(
                                ingredientDraft.ingredient,
                                onAction,
                                draftIndex,
                                Modifier.width(125.dp)
                            )
                            IconButton(onClick = {
                                onAction(
                                    CreateRecipeAction.OnDeleteIngredient(
                                        draftIndex
                                    )
                                )
                            }) {
                                Icon(
                                    imageVector = DeleteIcon,
                                    contentDescription = stringResource(id = R.string.cd_delete_ingredient)
                                )
                            }
                        }
                    }

                }
            }
        }
        AddListingButton { onAction(CreateRecipeAction.OnAddEmptyIngredient) }
    }
}

@Composable
private fun IngredientQuantityField(
    ingredientDraft: IngredientUI.QuantityIngredient,
    onAction: (CreateRecipeAction) -> Unit,
    draftIndex: Int,
    modifier: Modifier,
    isError: Boolean
) {
    TextField(
        value = ingredientDraft.quantity,
        onValueChange = { quantity ->
            onAction(
                CreateRecipeAction.OnIngredientQuantityChanged(
                    ingredientIndex = draftIndex,
                    quantity = quantity
                )
            )
        },
        maxLines = 1,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = {
            Text(
                text = stringResource(id = R.string.quantity),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        },
        isError = isError,
        supportingText = {
            if (isError){
                Text(text = stringResource(id = R.string.cant_be_empty))
            }
        }
    )
}

@Composable
private fun IngredientNameField(
    ingredientDraft: IngredientUI,
    onAction: (CreateRecipeAction) -> Unit,
    draftIndex: Int,
    modifier: Modifier,
    isError: Boolean
) {
    TextField(
        value = ingredientDraft.name,
        onValueChange = { name ->
            onAction(
                CreateRecipeAction.OnIngredientItemChanged(
                    ingredientIndex = draftIndex,
                    item = name
                )
            )
        },
        maxLines = 1,
        label = {
            Text(
                text = "${stringResource(id = R.string.item)} ${draftIndex + 1}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        },
        modifier = modifier,
        isError = isError,
        supportingText = {
            if (isError){
                Text(text = stringResource(id = R.string.cant_be_empty))
            }
        }
    )
}

@Composable
private fun UnitDropDown(
    ingredientDraft: IngredientUI,
    onAction: (CreateRecipeAction) -> Unit,
    draftIndex: Int,
    modifier: Modifier
) {
    var showUnitDropdown by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = showUnitDropdown,
        onExpandedChange = { showUnitDropdown = it },
        modifier = modifier
    ) {
        TextField(
            value = ingredientDraft.unit.toUiText().asString(),
            onValueChange = {},
            singleLine = true,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showUnitDropdown)
            },
            label = {
                Text(
                    text = stringResource(id = R.string.unit),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        )
        ExposedDropdownMenu(
            expanded = showUnitDropdown,
            onDismissRequest = { showUnitDropdown = false }) {
            IngredientUnit.entries.forEachIndexed { _, unit ->
                DropdownMenuItem(
                    text = { Text(unit.toUiText().asString()) },
                    onClick = {
                        onAction(
                            CreateRecipeAction.OnIngredientUnitChanged(
                                draftIndex,
                                unit
                            )
                        )
                        showUnitDropdown = false
                    })
            }
        }
    }
}

@Composable
private fun DurationInput(
    state: CreateRecipeState,
    onAction: (CreateRecipeAction) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = state.duration,
            onValueChange = { duration -> onAction(CreateRecipeAction.OnDurationChanged(duration)) },
            maxLines = 1,
            modifier = Modifier.width(150.dp),
            leadingIcon = {
                Icon(
                    imageVector = ClockIcon,
                    contentDescription = stringResource(id = R.string.duration)
                )
            },
            isError = state.showDurationError,
            supportingText = {
                if (state.showDurationError){
                    Text(text = stringResource(id = R.string.cant_be_empty))
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.minutes),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AddListingButton( onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = AddIcon, contentDescription = stringResource(id = R.string.cd_add_ingredient))
    }
}

@Composable
private fun ImageChooser(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    picture: Bitmap?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10))
            .then(if (picture == null) Modifier.height(120.dp) else Modifier)
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center
    ) {
        if (picture != null) {
            val imageBitmap = remember(picture) {
                picture.asImageBitmap()
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "",
                    contentScale = ContentScale.Fit
                )
            }
        }
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
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
                    onClick = { onCameraClick() },
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
        }

    }
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
private fun CreateRecipeScreenPreview() {
    RecipeHogTheme {
        Surface {
            CreateRecipeScreen(
                state = CreateRecipeState(),
                onAction = {},
                onCameraClick = {}
            )
        }
    }
}
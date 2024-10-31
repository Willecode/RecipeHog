package com.portfolio.recipe.presentation.create_recipe

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.model.IngredientListing
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.UiText
import com.portfolio.core.presentation.ui.asUiText
import com.portfolio.recipe.domain.RecipeDraft
import com.portfolio.recipe.domain.RecipeRepository
import com.portfolio.recipe.presentation.R
import com.portfolio.recipe.presentation.create_recipe.ingredient.IngredientDraft
import com.portfolio.recipe.presentation.create_recipe.ingredient.IngredientUI
import com.portfolio.recipe.presentation.create_recipe.ingredient.IngredientUnit
import com.portfolio.recipe.presentation.create_recipe.ingredient.util.asString
import com.portfolio.recipe.presentation.create_recipe.preparation.PreparationStep
import com.portfolio.recipe.presentation.create_recipe.tag.TagDraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

/**
 * Todo: This class became quite big and messy. Refactor it to make it more readable.
 */
class CreateRecipeViewModel(
    private val recipeRepository: RecipeRepository
): ViewModel() {

    var state by mutableStateOf(CreateRecipeState())
        private set

    private val _eventChannel = Channel<CreateRecipeEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    companion object{
        const val TITLE_MAX_CHARS = 30
        const val DESCRIPTION_MAX_CHARS = 100
        const val DURATION_MAX_CHARS = 4
        const val SERVINGS_MAX_CHARS = 4
        const val TAG_MAX_CHARS = 15
        const val INGREDIENT_NAME_MAX_CHARS = 50
        const val INGREDIENT_QUANTITY_MAX_CHARS = 5
        const val PREPARATION_STEP_MAX_CHARS = 100
        const val MAX_INGREDIENTS = 30
        const val MAX_PREPARATION_STEPS = 30
        const val MAX_TAGS = 5
    }

    fun onAction(action: CreateRecipeAction) {
        when(action) {
            CreateRecipeAction.OnAddEmptyIngredient -> {
                onAddEmptyIngredient()
            }
            CreateRecipeAction.OnAddPreparationStep -> {
                onAddPreparationStep()
            }
            CreateRecipeAction.DismissRationaleDialog -> {
                state = state.copy(showCameraPermissionRationale = false)
            }
            is CreateRecipeAction.OnDescriptionChanged -> {
                onDescriptionChanged(newDesc = action.newDesc)
            }
            is CreateRecipeAction.OnTitleChanged -> {
                onTitleChanged(newTitle = action.newTitle)
            }
            is CreateRecipeAction.OnDurationChanged -> {
                onDurationChanged(newDuration = action.newDuration)
            }
            is CreateRecipeAction.OnIngredientUnitChanged -> {
                onIngredientUnitChanged(
                    ingredientIndex = action.ingredientIndex,
                    unit = action.unit
                )
            }
            is CreateRecipeAction.OnIngredientItemChanged -> {
                onIngredientItemChanged(
                    ingredientIndex = action.ingredientIndex,
                    item = action.item
                )
            }
            is CreateRecipeAction.OnIngredientQuantityChanged -> {
                onIngredientQuantityChanged(
                    ingredientIndex = action.ingredientIndex,
                    quantity = action.quantity
                )
            }
            is CreateRecipeAction.OnPreparationStepChanged -> {
                onPreparationStepChanged(
                    stepIndex = action.stepIndex,
                    value = action.value
                )
            }
            is CreateRecipeAction.OnCameraPermissionChanged -> {
                onCameraPermissionChanged(action.hasCameraPermission)
            }
            is CreateRecipeAction.OnShowCameraPermRationaleChanged -> {
                state = state.copy(showCameraPermissionRationale = action.showCameraRationale)
            }
            is CreateRecipeAction.OnCameraError -> {
                onCameraError()
            }
            is CreateRecipeAction.OnPictureTaken -> {
                onPictureTaken(action.picture)
            }
            is CreateRecipeAction.OnDeleteIngredient -> {
                onDeleteIngredient(ingredientIndex = action.ingredientIndex)
            }
            is CreateRecipeAction.OnDeletePreparationStep -> {
                onDeletePreparationStep(stepIndex = action.stepIndex)
            }
            is CreateRecipeAction.OnServingsChanged -> {
                onServingsChanged(action.newServings)
            }
            is CreateRecipeAction.OnPostClick -> {
                postRecipe(action.filesDirectory)
            }
            is CreateRecipeAction.OnAddTag -> {
                onAddTag()
            }
            is CreateRecipeAction.OnDeleteTag -> {
                onDeleteTag(tagIndex = action.tagIndex)
            }
            is CreateRecipeAction.OnTagChanged -> {
                onTagChanged(tagIndex = action.tagIndex, value = action.value)
            }
            else -> {
                Unit
            }
        }
    }

    private fun onTagChanged(tagIndex: Int, value: String) {
        val newTag = value.take(TAG_MAX_CHARS)
        updateTag(tagIndex, newTag)
    }

    private fun updateTag(tagIndex: Int, newTag: String) {
        val newTags = state.tags.toMutableList()
        newTags[tagIndex] = TagDraft(text = newTag, showError = false)
        state = state.copy(tags = newTags)
    }

    private fun onDeleteTag(tagIndex: Int) {
        val tags = state.tags.toMutableList()
        tags.removeAt(tagIndex)
        state = state.copy(tags = tags)
    }

    private fun onAddTag() {
        if (MAX_TAGS <= state.tags.size) {
            viewModelScope.launch {
                _eventChannel.send(CreateRecipeEvent.Error(UiText.StringResource(R.string.reached_max_tag_count)))
                return@launch
            }
        } else {
            val newTags = state.tags.toMutableList().apply {
                add(TagDraft(text = "", showError = false))
            }
            state = state.copy(tags = newTags)
        }
    }

    private fun onServingsChanged(newServings: String) {
        if (newServings.isDigitsOnly())
            state = state.copy(servings = newServings.take(SERVINGS_MAX_CHARS))
    }

    private fun onDurationChanged(newDuration: String) {
        if (newDuration.isDigitsOnly())
            state = state.copy(duration = newDuration.take(DURATION_MAX_CHARS))
    }

    private fun onTitleChanged(newTitle: String) {
        state = state.copy(title = newTitle.take(TITLE_MAX_CHARS))
    }

    private fun onDescriptionChanged(newDesc: String) {
        state = state.copy(description = newDesc.take(DESCRIPTION_MAX_CHARS))
    }

    private fun postRecipe(filesDirectory: String) {
        viewModelScope.launch {
            state = state.copy(posting = true)

            // Validations
            if (!validateInputs()){
                _eventChannel.send(CreateRecipeEvent.Error(UiText.StringResource(R.string.form_contains_invalid_entries)))
                state = state.copy(posting = false)
                return@launch
            }

            val file =
                withContext(context = Dispatchers.IO) {
                    savePic(filesDirectory)
                }
            if (!file.exists()){
                _eventChannel.send(CreateRecipeEvent.Error(UiText.StringResource(R.string.error_couldnt_post_recipe)))
                state = state.copy(posting = false)
                return@launch
            }

            val result = recipeRepository.postRecipe(
                recipeDraft = createRecipeDraft(),
                imageFilePath = file.path
            )

            withContext(Dispatchers.IO) {
                file.delete()
            }

            when (result) {
                is Result.Error -> {
                    _eventChannel.send(CreateRecipeEvent.Error(result.error.asUiText()))
                    state = state.copy(posting = false)
                    if (result.error == DataError.Network.UNAUTHORIZED)
                        _eventChannel.send(CreateRecipeEvent.AuthError)
                }
                is Result.Success -> {
                    state = state.copy(posting = false)
                    _eventChannel.send(CreateRecipeEvent.RecipePostSuccessful)
                }
            }
        }
    }

    private fun createRecipeDraft() = RecipeDraft(
        title = state.title,
        description = state.description,
        duration = state.duration.toInt(),
        servings = state.servings.toInt(),
        ingredientDrafts = state.ingredientDrafts.map { ingredientDraft ->
            when (val ingredient = ingredientDraft.ingredient) {
                is IngredientUI.NoQuantityIngredient -> {
                    IngredientListing(
                        name = ingredient.name,
                        quantity = null,
                        unit = ingredient.unit.asString()
                    )
                }

                is IngredientUI.QuantityIngredient -> {
                    IngredientListing(
                        name = ingredient.name,
                        quantity = ingredient.quantity.toFloat(),
                        unit = ingredient.unit.asString()
                    )
                }
            }
        },
        preparationSteps = state.preparationSteps.map { it.text },
        tags = state.tags.map { it.text }
    )

    // TODO: Optimization: This assigns a new value to state multiple times with "state = state.copy(...), could be done with a single assignment
    private fun validateInputs(): Boolean {
        val titleValid = validateTitle()
        val descValid = validateDescription()
        val durationValid = validateDuration()
        val servingsValid = validateServings()
        val ingredientsValid = validateIngredientDrafts()
        val preparationValid = validatePreparationSteps()
        val tagsValid = validateTags()

        return (
            titleValid && descValid && durationValid && servingsValid && ingredientsValid && preparationValid && tagsValid
        )
    }

    private fun validateTags(): Boolean {
        var valid = true
        val tags = state.tags.map { tag ->
            val tagValid = tag.text.isNotBlank()
            if (!tagValid)
                valid = false
            TagDraft(
                text = tag.text,
                showError = !tagValid
            )
        }

        state = state.copy(tags = tags)

        return valid
    }

    private fun validatePreparationSteps(): Boolean {
        var valid = true
        val steps = state.preparationSteps.map {step ->
            val stepValid = step.text.isNotBlank()
            if (!stepValid)
                valid = false
            PreparationStep(
                text = step.text,
                showError = !stepValid
            )
        }

        state = state.copy(preparationSteps = steps)

        return valid
    }

    private fun validateIngredientDrafts(): Boolean {
        var valid = true
        val drafts = state.ingredientDrafts.map {draft ->
            val nameValid = draft.ingredient.name.isNotBlank()
            val quantityValid = when(draft.ingredient) {
                is IngredientUI.NoQuantityIngredient -> true
                is IngredientUI.QuantityIngredient -> draft.ingredient.quantity.isNotBlank()
            }
            if (!(nameValid && quantityValid))
                valid = false
            IngredientDraft(
                ingredient = draft.ingredient,
                showQuantityError = !quantityValid,
                showNameError = !nameValid
            )
        }

        state = state.copy(ingredientDrafts = drafts)
        return valid
    }

    private fun validateServings(): Boolean {
        val valid = state.servings.isNotBlank()
        state = state.copy(showServingsError = !valid)
        return valid
    }

    private fun validateDuration(): Boolean {
        val valid = state.duration.isNotBlank()
        state = state.copy(showDurationError = !valid)
        return valid
    }

    private fun validateDescription(): Boolean {
        val valid = state.description.isNotBlank()
        state = state.copy(showDescriptionError = !valid)
        return valid
    }

    private fun validateTitle(): Boolean {
        val valid = state.title.isNotBlank()
        state = state.copy(showTitleError = !valid)
        return valid
    }

    private suspend fun savePic(filesDirectory: String): File {
        return withContext(context = Dispatchers.IO){
            val outputDir = File(filesDirectory, "temp_images") //TODO: Add file name constant
            outputDir.mkdirs()
            val file = File(outputDir, "${UUID.randomUUID().toString()}.jpg")
            if (state.picture == null) {
                viewModelScope.launch(Dispatchers.Default) {
                    _eventChannel.send(CreateRecipeEvent.Error(UiText.StringResource(R.string.please_add_a_picture)))
                }
                return@withContext file
            }

            if (outputDir.exists()) {
                try {
                    val outputStream = FileOutputStream(file)
                    state.picture?.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) {
                    viewModelScope.launch(Dispatchers.Default) {
                        _eventChannel.send(CreateRecipeEvent.Error(UiText.StringResource(R.string.error_couldnt_post_recipe)))
                    }
                }
            }
            return@withContext file
        }

    }

    private fun onPictureTaken(picture: Bitmap) {
        state = state.copy(picture = picture)
    }

    private fun onCameraError() {
        viewModelScope.launch {
            _eventChannel.send(CreateRecipeEvent.Error(UiText.StringResource(R.string.couldnt_access_device_camera)))
        }
    }

    private fun onCameraPermissionChanged(hasCameraPermission: Boolean) {
        state = state.copy(hasCameraPermission = hasCameraPermission)
        if (!hasCameraPermission) {
            viewModelScope.launch {
                _eventChannel.send(CreateRecipeEvent.Error(UiText.StringResource(R.string.please_grant_camera_permission_in_app_settings)))
            }
        }
    }

    private fun onDeletePreparationStep(stepIndex: Int) {
        val steps = state.preparationSteps.toMutableList()
        steps.removeAt(stepIndex)
        state = state.copy(preparationSteps = steps)
    }

    private fun onDeleteIngredient(ingredientIndex: Int) {
        val ingredients = state.ingredientDrafts.toMutableList()
        ingredients.removeAt(ingredientIndex)
        state = state.copy(ingredientDrafts = ingredients)
    }

    private fun onAddPreparationStep() {
        if (MAX_PREPARATION_STEPS <= state.preparationSteps.size) {
            viewModelScope.launch {
                _eventChannel.send(CreateRecipeEvent.Error(UiText.StringResource(R.string.reached_max_preparation_steps)))
                return@launch
            }
        } else {
            val newSteps = state.preparationSteps.toMutableList().apply {
                add(PreparationStep(text = "", showError = false))
            }
            state = state.copy(preparationSteps = newSteps)
        }

    }

    private fun onPreparationStepChanged(stepIndex: Int, value: String) {
        val newStep = value.take(PREPARATION_STEP_MAX_CHARS)
        updatePreparationStep(stepIndex = stepIndex, value = newStep)
    }

    private fun updatePreparationStep(
        stepIndex: Int,
        value: String
    ) {
        val newSteps = state.preparationSteps.toMutableList()
        newSteps[stepIndex] = PreparationStep(text = value, showError = false)

        state = state.copy(
            preparationSteps = newSteps
        )
    }

    private fun onAddEmptyIngredient() {
        if (MAX_INGREDIENTS <= state.ingredientDrafts.size) {
            viewModelScope.launch {
                _eventChannel.send(CreateRecipeEvent.Error(UiText.StringResource(R.string.reached_max_ingredients)))
                return@launch
            }
        } else {
            addEmptyIngredient()
        }

    }

    private fun addEmptyIngredient() {
        val newIngredients = state.ingredientDrafts.toMutableStateList().apply {
            add(IngredientDraft(
                ingredient = IngredientUI.QuantityIngredient(),
                showQuantityError = false,
                showNameError = false
            ))
        }
        state = state.copy(
            ingredientDrafts = newIngredients
        )
    }

    private fun onIngredientQuantityChanged(ingredientIndex: Int, quantity: String) {
        if (!quantity.isDigitsOnly())
            return

        val ingredient = state.ingredientDrafts[ingredientIndex].ingredient

        val newQuantity = quantity.take(INGREDIENT_QUANTITY_MAX_CHARS)

        val newIngredient = when (ingredient) {
            is IngredientUI.NoQuantityIngredient -> ingredient
            is IngredientUI.QuantityIngredient -> IngredientUI.QuantityIngredient(
                name = ingredient.name, unit = ingredient.unit, quantity = newQuantity
            )
        }

        updateIngredient(ingredientIndex, newIngredient)
    }

    private fun onIngredientItemChanged(ingredientIndex: Int, item: String) {
        val ingredient = state.ingredientDrafts[ingredientIndex].ingredient

        val newName = item.take(INGREDIENT_NAME_MAX_CHARS)

        val newIngredient = when (ingredient) {
            is IngredientUI.NoQuantityIngredient -> IngredientUI.NoQuantityIngredient(
                name = newName, unit = ingredient.unit
            )
            is IngredientUI.QuantityIngredient -> IngredientUI.QuantityIngredient(
                name = newName, unit = ingredient.unit, quantity = ingredient.quantity
            )
        }

        updateIngredient(ingredientIndex, newIngredient)
    }

    private fun updateIngredient(
        ingredientIndex: Int,
        newIngredient: IngredientUI
    ) {
        val newIngredients = state.ingredientDrafts.toMutableStateList()
        newIngredients[ingredientIndex] = IngredientDraft(
            ingredient = newIngredient,
            showQuantityError = false,
            showNameError = false
        )

        state = state.copy(
            ingredientDrafts = newIngredients
        )
    }

    private fun onIngredientUnitChanged(ingredientIndex: Int, unit: IngredientUnit) {
        val ingredient = state.ingredientDrafts[ingredientIndex].ingredient

        val newIngredient =
            if (unit == IngredientUnit.ToTaste){
                IngredientUI.NoQuantityIngredient(name = ingredient.name, unit = unit)
            } else {
                IngredientUI.QuantityIngredient(
                    name = ingredient.name,
                    unit = unit,
                    quantity = if (ingredient is IngredientUI.QuantityIngredient) ingredient.quantity else "1"
                )
            }

        updateIngredient(ingredientIndex, newIngredient)
    }
}
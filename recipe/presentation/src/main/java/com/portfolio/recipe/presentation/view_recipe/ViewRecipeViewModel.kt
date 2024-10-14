package com.portfolio.recipe.presentation.view_recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.model.ReactiveUserDataRepository
import com.portfolio.core.domain.use_case.GetCurrentUserLikeAndBookmarkStateUseCase
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.UiText
import com.portfolio.core.presentation.ui.asUiText
import com.portfolio.recipe.domain.RecipeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ViewRecipeViewModel(
    private val recipeRepository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle,
    private val reactiveUserDataRepository: ReactiveUserDataRepository,
    private val getCurrentUserLikeAndBookmarkStateUseCase: GetCurrentUserLikeAndBookmarkStateUseCase
): ViewModel() {

    var state by mutableStateOf(ViewRecipeState())
        private set

    private val _eventChannel = Channel<ViewRecipeEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("recipeId")?.let {
                getCurrentUserLikeAndBookmarkStateUseCase.invoke(it).collect {bookmarkLikeState ->
                    state = state.copy(
                        isRecipeLiked = bookmarkLikeState.isLiked,
                        isRecipeBookmarked = bookmarkLikeState.isBookmarked
                    )
                }
            }
        }
        viewModelScope.launch {
            loadRecipe()
            fetchRecipe()
        }
        viewModelScope.launch {
            reactiveUserDataRepository.fetchCurrentUserData()
        }
    }

    private suspend fun loadRecipe() {
        state = state.copy(isLoading = true)
        savedStateHandle.get<String>("recipeId")?.let {
            when (val result = recipeRepository.getRecipeFromCache(it)) {
                is Result.Error -> {
                }

                is Result.Success -> {
                    state = state.copy(recipe = result.data)
                }
            }
        }
        state = state.copy(isLoading = false)
    }

    private suspend fun fetchRecipe() {
        savedStateHandle.get<String>("recipeId")?.let {
            when (val result = recipeRepository.getRecipeFromServer(it)) {
                is Result.Error -> {
                    _eventChannel.send(ViewRecipeEvent.ViewRecipeError(result.error.asUiText()))
                    if (state.recipe == null) {
                        state = state.copy(cantGetRecipe = true)
                    }
                }

                is Result.Success -> {
                    state = state.copy(recipe = result.data)
                }
            }
        }
    }

    private fun likeRecipe() {
        savedStateHandle.get<String>("recipeId")?.let{ recipeId ->
            viewModelScope.launch {
                val result = reactiveUserDataRepository.likeRecipe(recipeId)

                when (result) {
                    is Result.Error -> _eventChannel.send(
                        ViewRecipeEvent.ViewRecipeError(UiText.DynamicString("Oops"))
                    )
                    is Result.Success -> {
                        loadRecipe()
                    }
                }
            }
        }
    }

    private fun unlikeRecipe() {
        savedStateHandle.get<String>("recipeId")?.let{ recipeId ->
            viewModelScope.launch {
                val result = reactiveUserDataRepository.unlikeRecipe(recipeId)

                when (result) {
                    is Result.Error -> _eventChannel.send(
                        ViewRecipeEvent.ViewRecipeError(UiText.DynamicString("Oops"))
                    )
                    is Result.Success -> {
                        loadRecipe()
                    }
                }
            }
        }
    }

    private fun bookmarkRecipe() {
        savedStateHandle.get<String>("recipeId")?.let{ recipeId ->
            viewModelScope.launch {
                val result = reactiveUserDataRepository.bookmarkRecipe(recipeId)

                when (result) {
                    is Result.Error -> _eventChannel.send(
                        ViewRecipeEvent.ViewRecipeError(UiText.DynamicString("Oops"))
                    )
                    is Result.Success -> {
                    }
                }
            }
        }
    }

    private fun unbookmarkRecipe() {
        savedStateHandle.get<String>("recipeId")?.let{ recipeId ->
            viewModelScope.launch {
                val result = reactiveUserDataRepository.unbookmarkRecipe(recipeId)

                when (result) {
                    is Result.Error -> _eventChannel.send(
                        ViewRecipeEvent.ViewRecipeError(UiText.DynamicString("Oops"))
                    )
                    is Result.Success -> {
                    }
                }
            }
        }
    }

    private fun onLikeClick() {
        if (state.isRecipeLiked) {
            unlikeRecipe()
        } else {
            likeRecipe()
        }
    }

    private fun onBookmarkClick() {
        if (state.isRecipeBookmarked) {
            unbookmarkRecipe()
        } else {
            bookmarkRecipe()
        }
    }

    fun onAction(action: ViewRecipeAction) {
        when (action) {
            ViewRecipeAction.OnBackPress -> Unit
            ViewRecipeAction.OnLikeClicked -> onLikeClick()
            ViewRecipeAction.OnBookmarkClicked -> onBookmarkClick()
        }
    }
}
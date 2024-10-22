package com.portfolio.recipe.presentation.view_recipe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.model.ReactiveUserDataRepository
import com.portfolio.core.domain.use_case.GetCurrentUserLikeAndBookmarkStateUseCase
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
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
            state = state.copy(isLoading = true)
            loadRecipe()
            fetchRecipe()
            state = state.copy(isLoading = false)
        }
        viewModelScope.launch {
            reactiveUserDataRepository.fetchCurrentUserData()
        }
    }

    private suspend fun loadRecipe() {
        savedStateHandle.get<String>("recipeId")?.let {
            when (val result = recipeRepository.getRecipeFromCache(it)) {
                is Result.Error -> {
                    handleError(error = result.error)
                }
                is Result.Success -> {
                    state = state.copy(recipe = result.data)
                }
            }
        }
    }

    private suspend fun sendAuthErrorEvent() {
        _eventChannel.send(ViewRecipeEvent.AuthError)
    }

    private suspend fun fetchRecipe() {
        savedStateHandle.get<String>("recipeId")?.let {
            when (val result = recipeRepository.getRecipeFromServer(it)) {
                is Result.Error -> {
                    handleError(result.error)
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

    private suspend fun ViewRecipeViewModel.handleError(
        error: DataError
    ) {
        if (error == DataError.Network.UNAUTHORIZED)
            _eventChannel.send(ViewRecipeEvent.AuthError)
        when (error) {
            DataError.Local.UNAVAILABLE -> Unit
            DataError.Network.UNAVAILABLE -> Unit
            else -> _eventChannel.send(ViewRecipeEvent.ViewRecipeError(error.asUiText()))
        }
    }

    private fun likeRecipe() {
        savedStateHandle.get<String>("recipeId")?.let{ recipeId ->
            viewModelScope.launch {
                val result = reactiveUserDataRepository.likeRecipe(recipeId)

                when (result) {
                    is Result.Error -> {
                        handleError(result.error)
                    }
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
                    is Result.Error -> {
                        handleError(result.error)
                    }
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
                    is Result.Error -> {
                        handleError(result.error)
                    }
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
                    is Result.Error -> {
                        handleError(result.error)
                    }
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
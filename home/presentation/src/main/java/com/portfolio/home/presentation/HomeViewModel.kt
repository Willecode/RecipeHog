package com.portfolio.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.UiText
import com.portfolio.home.domain.RecipeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class HomeViewModel(
    private val recipeRepository: RecipeRepository
): ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    private val _eventChannel = Channel<HomeEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            when (val result = recipeRepository.getRecommendedRecipes()) {
                is Result.Error -> {
                    _eventChannel.send(HomeEvent.HomeError(UiText.DynamicString("oops")))
                }
                is Result.Success -> {
                    state = state.copy(recipes = result.data)
                }
            }
        }
    }

    fun onAction(homeAction: HomeAction) {

    }
}
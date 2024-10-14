package com.portfolio.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.domain.DiscoverRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

//TODO: Collect flows in UI
class DiscoverViewModel(
    private val discoverRepository: DiscoverRepository
): ViewModel() {

    var state by mutableStateOf(DiscoverState())
        private set


    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _eventChannel = Channel<DiscoverEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {

        viewModelScope.launch {
            searchText
                .debounce(1000L)
                .onEach {
                    discoverRepository.fetchRecipesStart(20, "")
                }
                .collect{

                }
        }

        viewModelScope.launch {
            discoverRepository.getRecipes().collect {recipes ->
                state = state.copy(recipePreviews = recipes)
            }
        }

        viewModelScope.launch {
            discoverRepository.fetchRecipesStart(limit = 20, "")
        }
    }

    fun onAction(action: DiscoverAction) {
        when (action) {
            is DiscoverAction.onSearchTextChanged -> onSearchTextChanged(action.text)
        }
    }

    private fun onSearchTextChanged(text: String) {
        _searchText.value = text
    }
}
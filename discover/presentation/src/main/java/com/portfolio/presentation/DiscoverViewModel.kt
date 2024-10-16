@file:OptIn(FlowPreview::class)

package com.portfolio.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.asUiText
import com.portfolio.domain.DiscoverRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val discoverRepository: DiscoverRepository
): ViewModel() {

    var state by mutableStateOf(DiscoverState())
        private set

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    val recipePreviews = discoverRepository.getRecipes()
        .onEach {
            state = state.copy(reachedEndOfData = it.reachedEndOfData)
        }.map {
            it.recipes
        }


    private val _eventChannel = Channel<DiscoverEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            state = state.copy(loading = true)
            initState()
            state = state.copy(loading = false, searchEnabled = true)
        }

        viewModelScope.launch {
            searchText
                .debounce(1000L)
                .collect{
                    when(state.offlineMode) {
                        true -> loadInitialRecipesFromCache()
                        false -> fetchInitialRecipesFromNetwork()
                    }
                }
        }
    }

    private suspend fun initState() {
        loadInitialRecipesFromCache()
        when (fetchInitialRecipesFromNetwork()) {
            is Result.Error ->  state = state.copy(offlineMode = true)
            is Result.Success -> state = state.copy(offlineMode = false)
        }
    }

    private suspend fun fetchInitialRecipesFromNetwork(): EmptyResult<DataError.Network> {
        val result = discoverRepository.fetchInitialRecipesFromServer(
            limit = FETCH_LIMIT,
            titleQuery = searchText.value
        )
        when (result) {
            is Result.Error -> {
                when (result.error) {
                    DataError.Network.UNAUTHORIZED -> TODO()
                    else -> {
                        _eventChannel.send(DiscoverEvent.Error(result.error.asUiText()))
                    }
                }
            }
            is Result.Success -> Unit
        }
        return result
    }

    private suspend fun loadInitialRecipesFromCache(): EmptyResult<DataError.Network> {
        val result = discoverRepository.loadInitialRecipesFromCache(
            limit = FETCH_LIMIT,
            titleQuery = searchText.value
        )
        when (result) {
            is Result.Error -> _eventChannel.send(DiscoverEvent.Error(result.error.asUiText()))
            is Result.Success -> Unit
        }
        return result
    }

    fun onAction(action: DiscoverAction) {
        when (action){
            is DiscoverAction.OnSearchTextChanged -> onSearchTextChanged(action.text)
            DiscoverAction.OnRecipeListEndReached -> fetchMoreRecipes()
        }
    }

    private fun onSearchTextChanged(text: String) {
        state = state.copy(reachedEndOfData = false)
        _searchText.value = text
    }

    private fun fetchMoreRecipes() {
        viewModelScope.launch {
            state = state.copy(loadingMoreRecipes = true, throttlingGateOpen = false)
            when (state.offlineMode) {
                true -> loadMoreRecipesFromCache()
                false -> fetchMoreRecipesFromServer()
            }
            state = state.copy(loadingMoreRecipes = false)
            delay(1_000L)
            state = state.copy(throttlingGateOpen = true)
        }
    }

    private suspend fun loadMoreRecipesFromCache() {
        val result = discoverRepository.loadMoreRecipesFromCache(
            limit = FETCH_LIMIT,
            titleQuery = searchText.value
        )
        when (result) {
            is Result.Error -> _eventChannel.send(DiscoverEvent.Error(result.error.asUiText()))
            is Result.Success -> Unit
        }
    }

    private suspend fun fetchMoreRecipesFromServer() {
        val result = discoverRepository.fetchMoreRecipesFromServer(
            limit = FETCH_LIMIT,
            titleQuery = searchText.value
        )
        when (result) {
            is Result.Error -> _eventChannel.send(DiscoverEvent.Error(result.error.asUiText()))
            is Result.Success -> Unit
        }
    }

    companion object{
        const val FETCH_LIMIT = 10
        const val EMPTY_QUERY = ""
    }

}
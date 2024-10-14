package com.portfolio.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.model.SessionStorage
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.UiText
import com.portfolio.core.presentation.ui.asUiText
import com.portfolio.home.domain.HomeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val sessionStorage: SessionStorage
): ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    private val _eventChannel = Channel<HomeEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            loadContentFromCache()
            loadContentFromServer()
        }
        state = state.copy(userName = sessionStorage.get()?.userName)
    }

    private suspend fun loadContentFromCache() {
        state = state.copy(isLoading = true)
        when (val result = homeRepository.getContentFromCache()) {
            is Result.Error -> {
                _eventChannel.send(HomeEvent.HomeError(UiText.DynamicString("oops")))
            }
            is Result.Success -> {
                state = state.copy(contentList = result.data)
            }
        }
        state = state.copy(isLoading = false)
    }

    private suspend fun loadContentFromServer() {
        state = state.copy(isFetchingContent = true)
        when (val result = homeRepository.getContentFromServer()) {
            is Result.Error -> {
                _eventChannel.send(HomeEvent.HomeError(result.error.asUiText()))
            }
            is Result.Success -> {
                state = state.copy(contentList = result.data)
            }
        }
        state = state.copy(isFetchingContent = false)
    }

    fun onAction(homeAction: HomeAction) {

    }
}
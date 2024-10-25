package com.portfolio.profile.presentation.view_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.model.ReactiveUserDataRepository
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ViewProfileViewModel(
    userDataRepository: ReactiveUserDataRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var state by mutableStateOf(ViewProfileState())
        private set

    private val _eventChannel = Channel<ViewProfileEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("userId")?.let { userId ->
            viewModelScope.launch {
                userDataRepository.getUserData(userId).collect {userdata ->
                    state = state.copy(publicUserData = userdata.publicUserData)
                }
            }
            viewModelScope.launch {
                when (val result = userDataRepository.fetchUserData(userId)) {
                    is Result.Error -> handleError(result.error)
                    is Result.Success -> Unit
                }
            }

        }
    }

    fun onAction(action: ViewProfileAction) {

    }

    private suspend fun handleError(error: DataError) {
        if (error == DataError.Network.UNAUTHORIZED)
            _eventChannel.send(ViewProfileEvent.AuthError)
        when (error) {
            DataError.Local.UNAVAILABLE -> Unit
            DataError.Network.UNAVAILABLE -> Unit
            else -> _eventChannel.send(ViewProfileEvent.ViewProfileError(error.asUiText()))
        }
    }
}
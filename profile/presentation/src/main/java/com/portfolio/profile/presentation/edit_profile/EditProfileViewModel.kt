package com.portfolio.profile.presentation.edit_profile

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.core.domain.model.ReactiveUserDataRepository
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
import com.portfolio.core.presentation.ui.UiText
import com.portfolio.core.presentation.ui.asUiText
import com.portfolio.profile.presentation.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class EditProfileViewModel(
    private val userDataRepository: ReactiveUserDataRepository
): ViewModel() {
    var state by mutableStateOf(EditProfileState())
        private set

    init {
        viewModelScope.launch {
            userDataRepository.getCurrentUserData().collect {userdata ->
                state = state.copy(userData = userdata)
            }
        }
        viewModelScope.launch {
            when (val result = userDataRepository.fetchCurrentUserData()) {
                is Result.Error -> handleError(result.error)
                is Result.Success -> Unit
            }
        }
    }

    private val _eventChannel = Channel<EditProfileEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun onAction(action: EditProfileAction) {
        when (action) {
            EditProfileAction.DismissRationaleDialog -> {
                state = state.copy(showCameraPermissionRationale = false)
            }
            is EditProfileAction.OnCameraPermissionChanged -> {
                onCameraPermissionChanged(action.hasCameraPermission)
            }
            is EditProfileAction.OnShowCameraPermRationaleChanged -> {
                state = state.copy(showCameraPermissionRationale = action.showCameraRationale)
            }
            is EditProfileAction.OnCameraError -> {
                onCameraError()
            }
            is EditProfileAction.OnPictureTaken -> {
                onPictureTaken(action.picture)
            }
            is EditProfileAction.UploadProfilePic -> {
                uploadProfilePic(action.filesDirectory)
            }
        }
    }

    private fun uploadProfilePic(filesDirectory: String) {
        state.picture?.let {
            viewModelScope.launch {
                state = state.copy(uploadingProfilePicture = true)
                val file = savePic(filesDirectory)

                if (file.exists()) {
                    val result = userDataRepository.changeProfilePicture(file.path)
                    when (result) {
                        is Result.Error -> _eventChannel.send(
                            EditProfileEvent.EditProfileError(
                                UiText.StringResource(R.string.couldnt_change_profile_pic)
                            )
                        )
                        is Result.Success -> _eventChannel.send(
                            EditProfileEvent.ProfilePictureChangeSuccessful
                        )
                    }
                }

                withContext(Dispatchers.IO) {
                    file.delete()
                }
                state = state.copy(uploadingProfilePicture = false)
            }
        }
    }

    private suspend fun savePic(filesDirectory: String): File {
        return withContext(context = Dispatchers.IO){
            val outputDir = File(filesDirectory, "temp_images") //TODO: Add file name constant
            outputDir.mkdirs()
            val file = File(outputDir, "${UUID.randomUUID().toString()}.jpg")

            if (outputDir.exists()) {
                try {
                    val outputStream = FileOutputStream(file)
                    state.picture?.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) {
                    viewModelScope.launch(Dispatchers.Default) {
                        _eventChannel.send(EditProfileEvent.EditProfileError(UiText.StringResource(R.string.couldnt_change_profile_pic)))
                    }
                }
            }
            return@withContext file
        }

    }

    private fun onCameraPermissionChanged(hasCameraPermission: Boolean) {
        state = state.copy(hasCameraPermission = hasCameraPermission)
        if (!hasCameraPermission) {
            viewModelScope.launch {
                _eventChannel.send(EditProfileEvent.EditProfileError(UiText.StringResource(R.string.please_grant_camera_permission_in_app_settings)))
            }
        }
    }

    private fun onCameraError() {
        viewModelScope.launch {
            _eventChannel.send(EditProfileEvent.EditProfileError(UiText.StringResource(R.string.couldnt_access_device_camera)))
        }
    }

    private fun onPictureTaken(picture: Bitmap) {
        state = state.copy(picture = picture)
    }

    private suspend fun handleError(error: DataError) {
        if (error == DataError.Network.UNAUTHORIZED)
            _eventChannel.send(EditProfileEvent.AuthError)
        when (error) {
            DataError.Local.UNAVAILABLE -> Unit
            DataError.Network.UNAVAILABLE -> Unit
            else -> _eventChannel.send(EditProfileEvent.EditProfileError(error.asUiText()))
        }
    }

}
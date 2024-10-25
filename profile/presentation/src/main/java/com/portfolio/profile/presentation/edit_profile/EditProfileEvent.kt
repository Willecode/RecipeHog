package com.portfolio.profile.presentation.edit_profile

import com.portfolio.core.presentation.ui.UiText

interface EditProfileEvent {
    data class EditProfileError(val error: UiText): EditProfileEvent
    data object AuthError: EditProfileEvent
    data object ProfilePictureChangeSuccessful: EditProfileEvent
}
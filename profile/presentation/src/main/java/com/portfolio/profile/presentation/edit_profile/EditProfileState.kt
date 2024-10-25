package com.portfolio.profile.presentation.edit_profile

import android.graphics.Bitmap
import com.portfolio.core.domain.model.PublicUserData
import com.portfolio.core.domain.model.UserData
import java.time.LocalDate

data class EditProfileState(
    val userData: UserData = UserData(
        publicUserData = PublicUserData(
            displayName = "-",
            creationDate = LocalDate.now(),
            likes = 0,
            profilePictureUrl = "",
            postedRecipes = listOf()
        ),
        privateUserData = null
    ),

    val picture: Bitmap? = null,
    val uploadingProfilePicture: Boolean = false,
    /**
     * Permissions
     */
    val showCameraPermissionRationale: Boolean = false,
    val hasCameraPermission: Boolean = false,
)

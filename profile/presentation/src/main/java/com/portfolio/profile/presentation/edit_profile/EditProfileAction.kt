package com.portfolio.profile.presentation.edit_profile

import android.graphics.Bitmap


interface EditProfileAction {
    data class OnRecipeClick(val recipeId:String): EditProfileAction
    data class UploadProfilePic(val filesDirectory: String): EditProfileAction

    /**
     * Permissions
     */
    data class OnCameraPermissionChanged(val hasCameraPermission: Boolean): EditProfileAction
    data class OnShowCameraPermRationaleChanged(val showCameraRationale: Boolean): EditProfileAction
    data object DismissRationaleDialog: EditProfileAction
    data object OnRequestCameraPermission: EditProfileAction

    /**
     * Camera
     */
    data object OnCameraError: EditProfileAction
    data class  OnPictureTaken(val picture: Bitmap): EditProfileAction

}
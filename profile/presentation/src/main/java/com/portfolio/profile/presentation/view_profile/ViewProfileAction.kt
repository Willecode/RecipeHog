package com.portfolio.profile.presentation.view_profile

interface ViewProfileAction {
    data class OnRecipeClick(val recipeId:String): ViewProfileAction
}
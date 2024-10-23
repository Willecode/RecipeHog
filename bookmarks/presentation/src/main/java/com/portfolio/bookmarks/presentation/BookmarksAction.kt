package com.portfolio.bookmarks.presentation

interface BookmarksAction {
    data class OnRecipeClick(val recipeId:String): BookmarksAction
}
package com.portfolio.recipehog.navigation

import kotlinx.serialization.Serializable

// Auth destinations
@Serializable
object DestinationAuth
@Serializable
object DestinationIntro
@Serializable
object DestinationLogin
@Serializable
object DestinationRegister
@Serializable
object DestinationUserInfo

// Content destinations
@Serializable
object DestinationContent
@Serializable
object DestinationHome
@Serializable
object DestinationDiscover
@Serializable
data class DestinationViewRecipe(
    val recipeId: String
)
@Serializable
object DestinationCreateRecipe
@Serializable
object DestinationBookmarks
@Serializable
object DestinationYou
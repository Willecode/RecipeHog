package com.portfolio.profile.presentation.view_profile

import com.portfolio.core.domain.model.PublicUserData
import java.time.LocalDate

data class ViewProfileState(
    val publicUserData: PublicUserData = PublicUserData(
        displayName = "-",
        creationDate = LocalDate.now(),
        likes = 0,
        profilePictureUrl = "",
        postedRecipes = listOf()
    )
)

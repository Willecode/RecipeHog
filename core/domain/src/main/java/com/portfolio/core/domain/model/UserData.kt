package com.portfolio.core.domain.model

data class UserData(
    val publicUserData: PublicUserData,
    val privateUserData: PrivateUserData?
)

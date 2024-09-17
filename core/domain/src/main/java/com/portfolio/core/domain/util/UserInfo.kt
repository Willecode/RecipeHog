package com.portfolio.core.domain.util

/**
 * This could be extended with user preferences. Not yet sure if [userId] is needed to query
 * user info from backend, or if authentication happens internally in FirebaseAuth.
 */
data class UserInfo(
    val userId: String
)

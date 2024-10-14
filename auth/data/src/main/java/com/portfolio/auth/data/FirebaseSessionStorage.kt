package com.portfolio.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.portfolio.core.domain.model.SessionStorage
import com.portfolio.core.domain.util.UserInfo

class FirebaseSessionStorage(
    private val auth: FirebaseAuth
): SessionStorage {
    override fun get(): UserInfo? {
        return auth.currentUser?.toUserInfo()
    }
}

fun FirebaseUser.toUserInfo(): UserInfo {
    return UserInfo(userId = uid, userName = displayName)
}

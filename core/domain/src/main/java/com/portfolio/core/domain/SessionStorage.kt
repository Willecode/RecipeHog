package com.portfolio.core.domain

import com.portfolio.core.domain.util.UserInfo

interface SessionStorage {
    suspend fun get(): UserInfo?
}
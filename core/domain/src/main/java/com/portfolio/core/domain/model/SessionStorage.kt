package com.portfolio.core.domain.model

import com.portfolio.core.domain.util.UserInfo

interface SessionStorage {
    fun get(): UserInfo?
}
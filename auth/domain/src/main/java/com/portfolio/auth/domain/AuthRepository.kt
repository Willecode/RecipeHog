package com.portfolio.auth.domain

import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result

interface AuthRepository {
    suspend fun login(email:String, password: String): EmptyResult<DataError.Network>
    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
    suspend fun signOut(): EmptyResult<DataError.Network>
}
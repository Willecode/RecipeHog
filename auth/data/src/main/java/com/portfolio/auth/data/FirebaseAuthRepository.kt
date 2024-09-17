package com.portfolio.auth.data

import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.portfolio.auth.domain.AuthRepository
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth
): AuthRepository {
    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        return safeCall {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            when (result.user) {
                null -> Result.Error(DataError.Network.UNAUTHORIZED).asEmptyDataResult()
                else -> Result.Success(Unit).asEmptyDataResult()
            }
        }
    }

    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return safeCall {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            when (result.user) {
                null -> Result.Error(DataError.Network.UNAUTHORIZED).asEmptyDataResult()
                else -> Result.Success(Unit).asEmptyDataResult()
            }
        }
    }

    override suspend fun signOut(): EmptyResult<DataError.Network> {
        return safeCall {
            auth.signOut()
            return@safeCall Result.Success(Unit).asEmptyDataResult()
        }
    }

    private suspend fun safeCall(function: suspend () -> EmptyResult<DataError.Network>): EmptyResult<DataError.Network> {
        return try {
            function()
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error(DataError.Network.UNAUTHORIZED).asEmptyDataResult()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error(DataError.Network.UNAUTHORIZED).asEmptyDataResult()
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.Error(DataError.Network.UNAUTHORIZED).asEmptyDataResult()
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.Error(DataError.Network.UNAUTHORIZED).asEmptyDataResult()
        } catch (e: FirebaseTooManyRequestsException) {
            Result.Error(DataError.Network.TOO_MANY_REQUESTS).asEmptyDataResult()
        }
    }

}
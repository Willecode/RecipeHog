package com.portfolio.core.data.util

import com.google.firebase.firestore.FirebaseFirestoreException
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result

suspend inline fun <reified T> firestoreSafeCall(function: suspend () -> Result<T, DataError.Network>): Result<T, DataError.Network> {
    return try {
        function()
    } catch (e: FirebaseFirestoreException) {
        return when (e.code) {
            FirebaseFirestoreException.Code.UNAVAILABLE -> Result.Error(DataError.Network.NO_INTERNET)
            FirebaseFirestoreException.Code.UNAUTHENTICATED -> Result.Error(DataError.Network.UNAUTHORIZED)
            else -> Result.Error(DataError.Network.UNKNOWN)
        }
    }
}
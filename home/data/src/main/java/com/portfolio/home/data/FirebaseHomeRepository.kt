package com.portfolio.home.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.portfolio.core.data.util.firestoreSafeCallCache
import com.portfolio.core.data.util.firestoreSafeCallServer
import com.portfolio.core.domain.model.HomeContent
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
import com.portfolio.home.data.model.HomeContentSerializableMulti
import com.portfolio.home.data.model.HomeContentSerializableSingle
import com.portfolio.home.data.model.toMultiRecipePreview
import com.portfolio.home.data.model.toSingleRecipePreview
import com.portfolio.home.domain.HomeRepository
import kotlinx.coroutines.tasks.await

class FirebaseHomeRepository(
    private val firestore: FirebaseFirestore
): HomeRepository {

    override suspend fun getContentFromCache(): Result<List<HomeContent>, DataError> {
        return firestoreSafeCallCache {
            getContent(source = Source.CACHE)
        }
    }

    override suspend fun getContentFromServer(): Result<List<HomeContent>, DataError.Network> {
       return firestoreSafeCallServer {
           getContent(source = Source.SERVER)
       }
    }

    private suspend fun getContent(source: Source): Result<List<HomeContent>, DataError.Network> {
        val snapshot = firestore.collection("homeContent")
            .get(source)
            .await()

        val data = snapshot.documents.mapNotNull {
            when (it?.data) {
                null -> null
                else -> {
                    val content = it.data!!["content"]
                    if (content == null) {
                        it.toObject(HomeContentSerializableSingle::class.java)!!
                            .toSingleRecipePreview()
                    } else {
                        it.toObject(HomeContentSerializableMulti::class.java)!!
                            .toMultiRecipePreview()
                    }
                }
            }
        }

        return Result.Success(data)
    }
}
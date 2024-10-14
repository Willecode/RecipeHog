package com.portfolio.home.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.portfolio.core.domain.model.HomeContent
import com.portfolio.core.domain.model.HomeContent.MultiRecipePreview
import com.portfolio.core.domain.model.HomeContent.SingleRecipePreview
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result
import com.portfolio.home.domain.HomeRepository
import kotlinx.coroutines.tasks.await

class FirebaseHomeRepository(
    private val firestore: FirebaseFirestore
): HomeRepository {
    override suspend fun getContent(): Result<List<HomeContent>, DataError.Network> {
        return getContent(source = Source.DEFAULT)
    }

    override suspend fun getContentFromCache(): Result<List<HomeContent>, DataError.Network> {
        return getContent(source = Source.CACHE)
    }

    override suspend fun getContentFromServer(): Result<List<HomeContent>, DataError.Network> {
       return getContent(source = Source.SERVER)
    }

    private suspend fun getContent(source: Source): Result<List<HomeContent>, DataError.Network> {
        try {
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
        } catch (e: FirebaseFirestoreException) {
            return when (e.code) {
//                FirebaseFirestoreException.Code.CANCELLED -> TODO()
//                FirebaseFirestoreException.Code.UNKNOWN -> TODO()
//                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> TODO()
//                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> TODO()
//                FirebaseFirestoreException.Code.NOT_FOUND -> TODO()
//                FirebaseFirestoreException.Code.ALREADY_EXISTS -> TODO()
//                FirebaseFirestoreException.Code.PERMISSION_DENIED -> TODO()
//                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> TODO()
//                FirebaseFirestoreException.Code.FAILED_PRECONDITION -> TODO()
//                FirebaseFirestoreException.Code.ABORTED -> TODO()
//                FirebaseFirestoreException.Code.OUT_OF_RANGE -> TODO()
//                FirebaseFirestoreException.Code.UNIMPLEMENTED -> TODO()
//                FirebaseFirestoreException.Code.INTERNAL -> TODO()
//                FirebaseFirestoreException.Code.DATA_LOSS -> TODO()
                FirebaseFirestoreException.Code.UNAVAILABLE -> Result.Error(DataError.Network.NO_INTERNET)
                FirebaseFirestoreException.Code.UNAUTHENTICATED -> Result.Error(DataError.Network.UNAUTHORIZED)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }
    }

    data class HomeContentSerializableSingle(
        val title: String = "",
        val recipeTitle: String = "",
        val author: String = "",
        val description: String = "",
        val imgUrl: String = "",
        val recipeId: String = ""
    )

    private fun HomeContentSerializableSingle.toSingleRecipePreview(): SingleRecipePreview {
        return SingleRecipePreview(
            title = title,
            recipe = RecipePreview(
                title = recipeTitle,
                author = author,
                description = description,
                imgUrl = imgUrl,
                recipeId = recipeId
            )
        )
    }

    data class HomeContentSerializableMulti(
        val title: String = "",
        val content: List<Map<String, String>> = listOf()
    )

    private fun HomeContentSerializableMulti.toMultiRecipePreview(): MultiRecipePreview {
        return MultiRecipePreview(
            title = title,
            recipes = content.map {
                RecipePreview(
                    title = it["title"] ?: "",
                    author = it["author"] ?: "",
                    description = it["description"] ?: "",
                    imgUrl = it["imgUrl"] ?: "",
                    recipeId = it["recipeId"] ?: ""
                )
            }
        )
    }
}
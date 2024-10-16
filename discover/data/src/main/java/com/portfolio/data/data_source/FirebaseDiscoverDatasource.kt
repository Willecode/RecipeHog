package com.portfolio.data.data_source

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.portfolio.core.data.FirebaseConstants.RECIPE_PREVIEWS_COLLECTION
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.domain.util.asEmptyDataResult
import com.portfolio.domain.PaginatedRecipePreviewList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class FirebaseDiscoverDatasource(
    private val firestore: FirebaseFirestore
): DiscoverDataSource {

    private val _documentFlow = MutableStateFlow<PaginatedDocumentSnapshots>(
        PaginatedDocumentSnapshots(documents = listOf(), reachedEndOfData = false)
    )

    override fun getRecipes(): Flow<PaginatedRecipePreviewList> = _documentFlow.map { docs ->
        PaginatedRecipePreviewList(
            reachedEndOfData = docs.reachedEndOfData,
            recipes = docs.documents.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(RecipePreviewSerializable::class.java)
                    ?.toRecipePreview()?.copy(recipeId = documentSnapshot.id)
            }
        )
    }

    override suspend fun loadInitialRecipesFromCache(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {
        return fetchInitialRecipes(limit = limit, titleQuery = titleQuery, source = Source.CACHE)
    }

    override suspend fun loadMoreRecipesFromCache(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {
        return fetchMoreRecipes(limit = limit, titleQuery = titleQuery, source = Source.CACHE)
    }

    override suspend fun fetchInitialRecipesFromServer(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {
        return fetchInitialRecipes(limit = limit, titleQuery = titleQuery, source = Source.SERVER)
    }

    override suspend fun fetchMoreRecipesFromServer(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {
        return fetchMoreRecipes(limit = limit, titleQuery = titleQuery, source = Source.SERVER)
    }

    private suspend fun fetchInitialRecipes(
        limit: Int,
        titleQuery: String,
        source: Source
    ): EmptyResult<DataError.Network> {

        return safeCall{
            val querySnapshot = firestore
                .collection(RECIPE_PREVIEWS_COLLECTION)
                .whereGreaterThanOrEqualTo("title", titleQuery)
                .whereLessThanOrEqualTo("title", titleQuery + "\uf8ff")
                .orderBy("title")
                .limit(limit.toLong())
                .get(source)
                .await()

            _documentFlow.update {
                PaginatedDocumentSnapshots(
                    documents = querySnapshot.documents,
                    reachedEndOfData = querySnapshot.documents.size < limit
                )
            }

            return Result.Success(Unit).asEmptyDataResult()
        }
    }

    private suspend fun fetchMoreRecipes(
        limit: Int,
        titleQuery: String,
        source: Source
    ): EmptyResult<DataError.Network> {
        return safeCall{
            val querySnapshot = firestore
                .collection(RECIPE_PREVIEWS_COLLECTION)
                .whereGreaterThanOrEqualTo("title", titleQuery)
                .whereLessThanOrEqualTo("title", titleQuery + "\uf8ff")
                .orderBy("title")
                .startAfter(_documentFlow.value.documents.last())
                .limit(limit.toLong())
                .get(source)
                .await()

            val newDocs = _documentFlow.value.documents.toMutableList()
            newDocs.addAll(querySnapshot.documents)

            _documentFlow.update {
                PaginatedDocumentSnapshots(
                    documents = newDocs,
                    reachedEndOfData = querySnapshot.documents.size < limit
                )
            }

            return Result.Success(Unit).asEmptyDataResult()
        }
    }

    private suspend inline fun <reified T> safeCall(function: suspend () -> Result<T, DataError.Network>): Result<T, DataError.Network> {
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

    data class RecipePreviewSerializable(
        val title: String = "",
        val author: String = "",
        val description: String = "",
        val imgUrl: String = "",
        val recipeId: String = ""
    )

    private fun RecipePreviewSerializable.toRecipePreview(): RecipePreview {
        return RecipePreview(
            title = title,
            author = author,
            description = description,
            imgUrl = imgUrl,
            recipeId = recipeId
        )
    }

    data class PaginatedDocumentSnapshots(
        val documents :List<DocumentSnapshot>,
        val reachedEndOfData: Boolean
    )
}
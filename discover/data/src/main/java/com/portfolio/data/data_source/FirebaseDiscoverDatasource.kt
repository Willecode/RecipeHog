package com.portfolio.data.data_source

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.portfolio.core.data.FirebaseConstants.RECIPE_PREVIEWS_COLLECTION
import com.portfolio.core.data.util.RecipePreviewSerializable
import com.portfolio.core.data.util.firestoreSafeCallCache
import com.portfolio.core.data.util.firestoreSafeCallServer
import com.portfolio.core.data.util.toRecipePreview
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
    ): EmptyResult<DataError> {
        return firestoreSafeCallCache {
            fetchInitialRecipes(limit = limit, titleQuery = titleQuery, source = Source.CACHE)
        }
    }

    override suspend fun loadMoreRecipesFromCache(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError> {
        return firestoreSafeCallCache {
            fetchMoreRecipes(limit = limit, titleQuery = titleQuery, source = Source.CACHE)
        }
    }

    override suspend fun fetchInitialRecipesFromServer(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {
        return firestoreSafeCallServer {
            fetchInitialRecipes(limit = limit, titleQuery = titleQuery, source = Source.SERVER)
        }
    }

    override suspend fun fetchMoreRecipesFromServer(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {
        return firestoreSafeCallServer {
            fetchMoreRecipes(limit = limit, titleQuery = titleQuery, source = Source.SERVER)
        }
    }

    private suspend fun fetchInitialRecipes(
        limit: Int,
        titleQuery: String,
        source: Source
    ): EmptyResult<DataError.Network> {
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

    private suspend fun fetchMoreRecipes(
        limit: Int,
        titleQuery: String,
        source: Source
    ): EmptyResult<DataError.Network> {
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

    data class PaginatedDocumentSnapshots(
        val documents :List<DocumentSnapshot>,
        val reachedEndOfData: Boolean
    )
}
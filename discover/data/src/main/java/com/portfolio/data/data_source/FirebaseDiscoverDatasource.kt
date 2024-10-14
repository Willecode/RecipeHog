package com.portfolio.data.data_source

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.portfolio.core.data.FirebaseConstants.RECIPE_PREVIEWS_COLLECTION
import com.portfolio.core.domain.model.RecipePreview
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.EmptyResult
import com.portfolio.core.domain.util.Result
import com.portfolio.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class FirebaseDiscoverDatasource(
    private val firestore: FirebaseFirestore
): DiscoverDataSource {

    private val _recipeFlow = MutableStateFlow<List<DocumentSnapshot>>(listOf())

    override fun getRecipes(): Flow<List<RecipePreview>> = _recipeFlow.map { list ->
        list.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(RecipePreviewSerializable::class.java)
                ?.toRecipePreview()
        }
    }

    override suspend fun fetchRecipesStart(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {

        val querySnapshot = firestore
            .collection(RECIPE_PREVIEWS_COLLECTION)
            .whereGreaterThanOrEqualTo("title", titleQuery)
            .whereLessThanOrEqualTo("title", titleQuery + "\uf8ff")
            .orderBy("title")
            .limit(limit.toLong())
            .get()
            .await()

//        val newDocs = _recipeFlow.value.toMutableList()
//        newDocs.addAll(querySnapshot.documents)

        _recipeFlow.update { querySnapshot.documents }

        return Result.Success(Unit).asEmptyDataResult()

    }

    override suspend fun fetchRecipesContinue(
        limit: Int,
        titleQuery: String
    ): EmptyResult<DataError.Network> {
        TODO("Not yet implemented")
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
}
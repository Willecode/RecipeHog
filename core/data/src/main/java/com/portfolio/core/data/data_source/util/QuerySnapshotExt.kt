package com.portfolio.core.data.data_source.util

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.portfolio.core.domain.model.PrivateUserData
import com.portfolio.core.domain.model.RecipePreview

/**
 * Todo: Could this be done by using a serializable data class with DocumentSnapshot.toObject()?
 */
fun QuerySnapshot.toPrivateUserData(): PrivateUserData {

    fun docSnapshotIntoContentMap(
        docSnapshot: QueryDocumentSnapshot,
        bookmarked: MutableMap<String, RecipePreview>
    ) {
        val map = docSnapshot.data.get("content") as? Map<String, Map<String, String>> ?: return
        map.forEach {
            bookmarked[it.key] = RecipePreview(
                title = it.value["title"] ?: throw (FirebaseFirestoreException(
                    "Private data deserialization failed.",
                    FirebaseFirestoreException.Code.UNKNOWN
                )),
                author = it.value["author"] ?: throw (FirebaseFirestoreException(
                    "Private data deserialization failed.",
                    FirebaseFirestoreException.Code.UNKNOWN
                )),
                description = it.value["description"] ?: throw (FirebaseFirestoreException(
                    "Private data deserialization failed.",
                    FirebaseFirestoreException.Code.UNKNOWN
                )),
                imgUrl = it.value["imgUrl"] ?: throw (FirebaseFirestoreException(
                    "Private data deserialization failed.",
                    FirebaseFirestoreException.Code.UNKNOWN
                )),
                recipeId = it.value["recipeId"] ?: throw (FirebaseFirestoreException(
                    "Private data deserialization failed.",
                    FirebaseFirestoreException.Code.UNKNOWN
                )),

                )
        }
    }

    val bookmarked: MutableMap<String, RecipePreview> = mutableMapOf()
    val liked: MutableMap<String, RecipePreview> = mutableMapOf()

    this.forEach{ docSnapshot ->
        if (docSnapshot.id == "likedRecipes") {
            docSnapshotIntoContentMap(docSnapshot, liked)
        }
        if (docSnapshot.id == "bookmarkedRecipes") {
            docSnapshotIntoContentMap(docSnapshot, bookmarked)
        }
    }

    return PrivateUserData(bookmarkedRecipes = bookmarked, likedRecipes = liked)
}
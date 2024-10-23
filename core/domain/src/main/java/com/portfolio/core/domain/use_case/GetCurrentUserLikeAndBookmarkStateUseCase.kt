package com.portfolio.core.domain.use_case

import com.portfolio.core.domain.model.ReactiveUserDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCurrentUserLikeAndBookmarkStateUseCase(
    private val reactiveUserDataRepository: ReactiveUserDataRepository
) {
    data class LikeAndBookmarkState(
        val isLiked: Boolean,
        val isBookmarked: Boolean
    )
    operator fun invoke(recipeId: String): Flow<LikeAndBookmarkState?> {
        return reactiveUserDataRepository.getCurrentUserData().map { userData ->
            if (userData.privateUserData == null) {
                null
            } else {
                LikeAndBookmarkState(
                    isLiked = userData.privateUserData.likedRecipes.containsKey(recipeId),
                    isBookmarked = userData.privateUserData.bookmarkedRecipes.containsKey(recipeId)
                )
            }
        }
    }
}
package com.portfolio.home.domain

import com.portfolio.core.domain.model.HomeContent
import com.portfolio.core.domain.util.DataError
import com.portfolio.core.domain.util.Result

interface HomeRepository {
    /**
     * Tries to fetch content from server. On failure, reads the content from the cache.
     */
    suspend fun getContent(): Result<List<HomeContent>, DataError.Network>

    /**
     * Reads content from cache.
     */
    suspend fun getContentFromCache(): Result<List<HomeContent>, DataError.Network>

    /**
     * Fetches content from the remote server.
     */
    suspend fun getContentFromServer(): Result<List<HomeContent>, DataError.Network>
}
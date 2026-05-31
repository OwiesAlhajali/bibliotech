package com.bibliotech.app.data.remote

import com.bibliotech.app.data.model.BookDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApiService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String
    ): OpenLibraryResponse
    @GET("{key}.json")
    suspend fun getBookDetails(
        @Path("key", encoded = true) bookKey: String
    ): BookDetailsResponse
}
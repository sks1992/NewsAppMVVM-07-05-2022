package sk.sandeep.newsapp.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import sk.sandeep.newsapp.model.NewsResponseDto
import sk.sandeep.newsapp.util_or_constants.Constants.Companion.API_KEY


/**
 * A public interface that exposes the [getBreakingNews] and [searchForNews] method
 */
interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY,
    ): Response<NewsResponseDto>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY,
    ): Response<NewsResponseDto>
}
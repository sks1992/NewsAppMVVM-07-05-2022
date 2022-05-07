package sk.sandeep.newsapp.model


/**
 * This data class defines a NewsResponse which includes an List of Articles,status and Total Result.
 */
data class NewsResponseDto(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)
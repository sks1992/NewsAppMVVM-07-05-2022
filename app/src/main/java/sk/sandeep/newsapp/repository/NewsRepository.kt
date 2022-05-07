package sk.sandeep.newsapp.repository


import sk.sandeep.newsapp.db.ArticleDatabase
import sk.sandeep.newsapp.model.Article
import sk.sandeep.newsapp.network.RetrofitInstance

class NewsRepository(
    val db: ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.newsApi.getBreakingNews(countryCode, pageNumber)


    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.newsApi.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticles(article)
}
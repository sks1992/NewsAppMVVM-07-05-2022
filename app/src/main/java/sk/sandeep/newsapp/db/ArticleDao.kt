package sk.sandeep.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import sk.sandeep.newsapp.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    @JvmSuppressWildcards
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    @JvmSuppressWildcards
    suspend fun deleteArticles(article: Article)

}
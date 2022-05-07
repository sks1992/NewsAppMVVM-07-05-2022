package sk.sandeep.newsapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * This data class defines a Article which includes id,author,content,description,
 publishedAt,source,title,url, and urlToImag,
 */

@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Serializable
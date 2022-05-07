package sk.sandeep.newsapp.view_model


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import sk.sandeep.newsapp.model.Article
import sk.sandeep.newsapp.model.NewsResponseDto
import sk.sandeep.newsapp.repository.NewsRepository
import sk.sandeep.newsapp.util_or_constants.Resource

class NewsViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _breakingNewsFlowData =
        MutableStateFlow<Resource<NewsResponseDto>>(Resource.Loading())
    val breakingNewsFlowData get() = _breakingNewsFlowData

    val breakingNews: MutableLiveData<Resource<NewsResponseDto>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponseDto? = null

    val searchNews: MutableLiveData<Resource<NewsResponseDto>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponseDto? = null


    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        _breakingNewsFlowData.emit(Resource.Loading())

        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        _breakingNewsFlowData.emit(handleBreakingNewsResponse(response))
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponseDto>): Resource<NewsResponseDto> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponseDto>): Resource<NewsResponseDto> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Viewmodel", "onCleared: viewmodel cleared")
    }
}
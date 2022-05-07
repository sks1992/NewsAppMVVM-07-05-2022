package sk.sandeep.newsapp.ui.fragments

import android.util.Log
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sk.sandeep.newsapp.R
import sk.sandeep.newsapp.adapter.NewsAdapter
import sk.sandeep.newsapp.databinding.FragmentSearchNewsBinding
import sk.sandeep.newsapp.model.Article
import sk.sandeep.newsapp.util_or_constants.Constants
import sk.sandeep.newsapp.util_or_constants.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import sk.sandeep.newsapp.util_or_constants.Resource
import sk.sandeep.newsapp.view_model.NewsViewModel


class SearchNewsFragment : BaseFragment<NewsViewModel, FragmentSearchNewsBinding>() {


    private lateinit var newsAdapter: NewsAdapter
    private val TAG = "SearchNewsFragment"

    var isLoading = false
    var isLastPage = false
    var isScrolling = false


    override fun setupUI() {

        setupRecyclerView()

        var job: Job? = null

        binding.etSearch.addTextChangedListener {
            job?.cancel()

            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)

                it?.let {
                    if (it.toString().isNotEmpty()) {
                        viewModel.searchNews(it.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchNewsPage == totalPages


                        if (isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Log.d(TAG, "Error: $it")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { article ->
            listItemClicked(article)
        }

        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    private fun listItemClicked(article: Article) {

        try {
            val action =
                SearchNewsFragmentDirections.actionSearchNewsFragmentToNewsFullViewFragment(article)
            findNavController().navigate(
                action
            )
        } catch (e: Exception) {
            Log.d("SaveNewsFragment", "listItemClicked: ${e.message}")
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.searchNews(binding.etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.isVisible = false
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.isVisible = true
        isLoading = true
    }

    override fun getContentView(): Int {
        return R.layout.fragment_search_news
    }

    override fun getViewModelClass(): Class<NewsViewModel> {
        return NewsViewModel::class.java
    }
}
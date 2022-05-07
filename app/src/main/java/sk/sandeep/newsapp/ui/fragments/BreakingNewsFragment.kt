package sk.sandeep.newsapp.ui.fragments

import android.annotation.SuppressLint
import android.util.Log
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sk.sandeep.newsapp.R
import sk.sandeep.newsapp.adapter.NewsAdapter
import sk.sandeep.newsapp.databinding.FragmentBreakingNewsBinding
import sk.sandeep.newsapp.model.Article
import sk.sandeep.newsapp.util_or_constants.Constants.Companion.QUERY_PAGE_SIZE
import sk.sandeep.newsapp.util_or_constants.Resource
import sk.sandeep.newsapp.view_model.NewsViewModel

class BreakingNewsFragment : BaseFragment<NewsViewModel, FragmentBreakingNewsBinding>() {

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    lateinit var newsAdapter: NewsAdapter

    private val TAG = "BreakingNewsFragment"


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun setupUI() {
        setupRecyclerView()

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.breakingNewsFlowData.collect { response ->
                    Log.d(TAG, "setupUI: ${viewModel.breakingNewsFlowData.replayCache}")

                    when (response) {
                        is Resource.Success -> {
                            hideProgressBar()
                            response.data?.let { newsResponse ->
                                newsAdapter.differ.submitList(newsResponse.articles.toList())
                                val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                                isLastPage = viewModel.breakingNewsPage == totalPages

                                if (isLastPage) {
                                    binding.rvBreakingNews.setPadding(0, 0, 0, 0)
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

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { article ->
            listItemClicked(article)
        }

        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }

    override fun getContentView(): Int {
        return R.layout.fragment_breaking_news
    }

    override fun getViewModelClass(): Class<NewsViewModel> {
        return NewsViewModel::class.java
    }


    private fun listItemClicked(article: Article) {

        try {
            val action =
                BreakingNewsFragmentDirections.actionBreakingNewsFragmentToNewsFullViewFragment(
                    article
                )
            findNavController().navigate(
                action
            )
        } catch (e: Exception) {
            Log.d(TAG, "listItemClicked: ${e.message}")
        }
    }
}

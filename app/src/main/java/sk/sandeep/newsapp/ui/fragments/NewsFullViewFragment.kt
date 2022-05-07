package sk.sandeep.newsapp.ui.fragments

import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import sk.sandeep.newsapp.R
import sk.sandeep.newsapp.databinding.FragmentNewsFullViewBinding
import sk.sandeep.newsapp.view_model.NewsViewModel


class NewsFullViewFragment : BaseFragment<NewsViewModel, FragmentNewsFullViewBinding>() {

    private val args by navArgs<NewsFullViewFragmentArgs>()

    override fun setupUI() {
        val article = args.article

        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(binding.root, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun getContentView(): Int {
        return R.layout.fragment_news_full_view

    }

    override fun getViewModelClass(): Class<NewsViewModel> {
        return NewsViewModel::class.java
    }
}
package sk.sandeep.newsapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import sk.sandeep.newsapp.R
import sk.sandeep.newsapp.databinding.ActivityNewsBinding
import sk.sandeep.newsapp.db.ArticleDatabase
import sk.sandeep.newsapp.repository.NewsRepository
import sk.sandeep.newsapp.view_model.NewsViewModel
import sk.sandeep.newsapp.view_model.NewsViewModelProviderFactory

/**
 * A Main NewsActivity that hosts all [Fragment]s for this application and hosts the nav controller.
 */
class NewsActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewsBinding
    lateinit var viewModel: NewsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_news)

        val newsRepository = NewsRepository(ArticleDatabase(this))

        val viewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[NewsViewModel::class.java]



        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.findNavController()
        /**
        For Adding BottomNavigation functionality
         * */
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}
package sk.sandeep.newsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sk.sandeep.newsapp.databinding.ItemArticlePreviewBinding

import sk.sandeep.newsapp.model.Article

class NewsAdapter(private val clickListener: (Article) -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.binding.root).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
        }

        holder.binding.root.setOnClickListener {
            clickListener(article)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class NewsViewHolder(val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root)
}
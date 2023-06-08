package com.example.fastcampusdeepen.SecondHandDeal.home

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fastcampusdeepen.SecondHandDeal.SecondHandDeal
import com.example.fastcampusdeepen.databinding.ItemArticleBinding
import java.sql.Date
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.days

class ArticleAdapter(val onItemClicked:(ArticleModel) -> Unit) : ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(articleModel: ArticleModel) {

//            val dateFormat = SimpleDateFormat("YYMMdd 00:00:00")
            val decimal = DecimalFormat("###,###")
            val priceFormat = decimal.format(articleModel.price.replace(",","").toLong())
            val systemDate = Calendar.getInstance()
            val date = Date(articleModel.createdAt)
            val gapDate = (systemDate.time.time -date.time)/(60*60*24*1000)
            Log.d("datt",systemDate.time.time.toString())
            Log.d("datt",date.time.toString())
            Log.d("datt",gapDate.toString())

            binding.articleTitle.text = articleModel.title
            binding.articleCreatedAt.text = "$gapDate 일전"
            binding.price.setText(priceFormat)
            binding.root.setOnClickListener {
                onItemClicked(articleModel)
            }
            if (articleModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.articleImage).load(articleModel.imageUrl)
                    .into(binding.articleImage)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(ItemArticleBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
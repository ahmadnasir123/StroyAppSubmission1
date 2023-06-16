package com.sirdev.storyapp.ui.home

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sirdev.storyapp.data.remote.response.stories.StoryItem
import com.sirdev.storyapp.databinding.StoryItemBinding
import com.sirdev.storyapp.ui.detail.DetailActivity

class HomeStoryAdapter : RecyclerView.Adapter<HomeStoryAdapter.ViewHolder>() {
    private val listStoryData = ArrayList<StoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listStoryData[position])
    }

    override fun getItemCount(): Int {
        return listStoryData.size
    }

    inner class ViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(storyImage)

                tvStoryTitle.text = story.name
                tvStoryDesc.text = story.description

                storyLayoutRoot.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)

                    intent.apply {
                        putExtra("IMAGE", story.photoUrl)
                        putExtra("DESC", story.description)
                        putExtra("NAME", story.name)
                    }

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(storyImage, "photo"),
                            Pair(tvStoryTitle, "name"),
                            Pair(tvStoryDesc, "description")
                        )

                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    fun setStoryData(story: List<StoryItem>) {
        val diffCallback = HomeStoryCallback(listStoryData, story)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listStoryData.clear()
        listStoryData.addAll(story)
        diffResult.dispatchUpdatesTo(this)
    }
}
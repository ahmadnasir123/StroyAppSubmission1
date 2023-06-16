package com.sirdev.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.sirdev.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private var activityDetailBinding: ActivityDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(activityDetailBinding!!.root)
        supportActionBar?.hide()
        showDetail()
    }

    private fun showDetail() {
        val name = intent.getStringExtra("NAME")
        val desc = intent.getStringExtra("DESC")
        val img = intent.getStringExtra("IMAGE")

        activityDetailBinding?.apply {
            tvStoryDetailDesc.text = desc
            tvStoryTitle.text = name
            Glide.with(this@DetailActivity)
                .load(img)
                .into(storyImage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityDetailBinding = null
    }
}
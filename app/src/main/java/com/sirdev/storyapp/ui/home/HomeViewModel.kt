package com.sirdev.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sirdev.storyapp.data.remote.WebService
import com.sirdev.storyapp.data.remote.response.stories.StoryItem
import com.sirdev.storyapp.data.remote.response.stories.StoryListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _listStoryData = MutableLiveData<List<StoryItem>>()
    val listStoryData: LiveData<List<StoryItem>> = _listStoryData

    private val _isLoading = MutableLiveData<Boolean>()

    private val _message = MutableLiveData<String>()

    private val services = WebService.create()

    fun getAllStoriesData(auth: String) {
        _isLoading.value = true
        services.getAllStories("Bearer $auth")
            .enqueue(object : Callback<StoryListResponse> {
                override fun onResponse(
                    call: Call<StoryListResponse>,
                    response: Response<StoryListResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _listStoryData.postValue(response.body()?.listStory)
                        _message.postValue(response.body()?.message)
                    } else {
                        _message.postValue(response.message())
                    }


                }

                override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = t.message
                }

            })
    }
}
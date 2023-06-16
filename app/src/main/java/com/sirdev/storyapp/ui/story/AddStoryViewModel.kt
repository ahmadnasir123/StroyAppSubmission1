package com.sirdev.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sirdev.storyapp.data.remote.WebService
import com.sirdev.storyapp.data.remote.response.stories.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel : ViewModel() {
    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> = _uploadStatus

    fun uploadStory(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ) {
        val service = WebService.create().uploadStory(token, imageMultipart, description)
        service.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _uploadStatus.value = responseBody != null && !responseBody.error
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                _uploadStatus.value = false
            }
        })
    }
}

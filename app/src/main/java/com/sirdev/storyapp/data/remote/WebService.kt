package com.sirdev.storyapp.data.remote

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.sirdev.storyapp.data.remote.response.auth.UserLoginResponse
import com.sirdev.storyapp.data.remote.response.auth.UserRegisterResponse
import com.sirdev.storyapp.data.remote.response.stories.AddStoryResponse
import com.sirdev.storyapp.data.remote.response.stories.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface  WebService {

    @FormUrlEncoded
    @POST(EndPoint.User.LOGIN)
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserLoginResponse>

    @FormUrlEncoded
    @POST(EndPoint.User.REGISTER)
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserRegisterResponse>

    @GET(EndPoint.Stories.GET_ALL_STORIES)
    fun getAllStories(
        @Header("Authorization") auth: String = TOKEN_SAMPLE
    ): Call<StoryListResponse>

    @Multipart
    @POST(EndPoint.Stories.GET_ALL_STORIES)
    fun uploadStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddStoryResponse>

    companion object {
        private const val TOKEN_SAMPLE =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWRLM0pZcnY1MWhkaFh4RmEiLCJpYXQiOjE2Njc3ODcyMTJ9.twKCJhU9Plxm3DEb5tEBHjLIDBxgQ7VRkz3Igw8eC5A"

        private val gson = GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        fun create(): WebService {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(EndPoint.BASE_URL)
                .client(okHttpClient)
                .build()
                .create(WebService::class.java)
        }
    }

    object EndPoint {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"

        object User {
            const val LOGIN = "login"
            const val REGISTER = "register"
        }

        object Stories {
            const val GET_ALL_STORIES = "stories"
        }
    }
}
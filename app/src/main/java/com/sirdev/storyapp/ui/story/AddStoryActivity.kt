package com.sirdev.storyapp.ui.story

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sirdev.storyapp.MainActivity
import com.sirdev.storyapp.data.remote.WebService
import com.sirdev.storyapp.data.remote.response.stories.AddStoryResponse
import com.sirdev.storyapp.databinding.ActivityAddStoryBinding
import com.sirdev.storyapp.utils.Preferences
import com.sirdev.storyapp.utils.UploadStoryUtils
import com.sirdev.storyapp.utils.UploadStoryUtils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var activityAddStoryBinding: ActivityAddStoryBinding

    private lateinit var currentPath: String
    private lateinit var userLoginPref: Preferences

    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(activityAddStoryBinding.root)
        userLoginPref = Preferences(this)
        supportActionBar?.title = "Upload Story"
        initView()
    }

    private fun initView() {
        activityAddStoryBinding.apply {
            btnCamera.setOnClickListener { openCamera() }
            btnGallery.setOnClickListener { openGallery() }
            btnUpload.setOnClickListener {
                uploadStory()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, "Permission is not granted", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        UploadStoryUtils.createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.sirdev.storyapp",
                it
            )
            currentPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraIntentLauncher.launch(intent)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun openGallery() {
        val intent = Intent()
        intent.apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }

        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        galleryIntentLauncher.launch(chooser)

    }

    private val cameraIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            activityAddStoryBinding.imgPreview.setImageBitmap(result)
        }
    }

    private val galleryIntentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = UploadStoryUtils.uriToFile(selectedImg, this@AddStoryActivity)

            getFile = myFile

            activityAddStoryBinding.imgPreview.setImageURI(selectedImg)
        }
    }


    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun uploadStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val descriptionText = activityAddStoryBinding.edtDesc.text.toString()
            val description = descriptionText.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val token = "Bearer ${userLoginPref.getLoginData().token}"
            val service = WebService.create().uploadStory(token, imageMultipart, description)
            service.enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddStoryActivity,
                                response.message(),
                                Toast.LENGTH_LONG
                            ).show()
                            onBackPressed()
                        } else {
                            Toast.makeText(
                                this@AddStoryActivity,
                                response.message(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    Toast.makeText(this@AddStoryActivity, t.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }

            })
        } else {
            Toast.makeText(
                this@AddStoryActivity,
                "Silahkan masukkan gambar terlebih dahulu.",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
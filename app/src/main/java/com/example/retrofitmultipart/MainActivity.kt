package com.example.retrofitmultipart

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.retrofitmultipart.data.UploadResponse
import com.example.retrofitmultipart.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var file: File
    private lateinit var bottomDialog: BottomSheetDialog

    private val galleryLauncher =
        this.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            try {
                val inputStream = contentResolver.openInputStream(it!!)
                file = File(applicationContext.filesDir, "image.jpg")
                inputStream?.copyTo(FileOutputStream(file))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private val cameraLauncher =
        this.registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            try {
                file = File(applicationContext.filesDir, "image.jpg")
                it!!.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private val permissionLauncher =
        this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            for (i in it) {
                if (i.value) {
                    Log.d(TAG, "Map Value: $i")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        )

        bottomDialog = BottomSheetDialog(this)
        val bottomView = layoutInflater.inflate(R.layout.bottomsheet, null)

        val closeBtn = bottomView.findViewById<ImageButton>(R.id.closeBtn)
        val galleryBtn = bottomView.findViewById<ImageButton>(R.id.galleryBtn)
        val cameraBtn = bottomView.findViewById<ImageButton>(R.id.cameraBtn)

        bottomDialog.setCancelable(false)
        bottomDialog.setContentView(bottomView)

        closeBtn.setOnClickListener {
            bottomDialog.dismiss()
        }

        galleryBtn.setOnClickListener {
            bottomDialog.dismiss()
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        cameraBtn.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) -> {
                    cameraLauncher.launch(null)
                }

                else -> {
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                }
            }
            bottomDialog.dismiss()
        }

        binding.imageView.setOnClickListener {
            bottomDialog.show()
        }

        binding.uploadImage.setOnClickListener {
            if (this::file.isInitialized) {
                sendUploadReq()
            } else {
                Toast.makeText(this, "No File Selected!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendUploadReq() {
        val retrofitBuilder =
            Retrofit.Builder().baseUrl("https://freeimage.host/api/1/").addConverterFactory(
                GsonConverterFactory.create()
            ).build().create(FreeImageApiService::class.java)

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

        val reqBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("key", "6d207e02198a847aa98d0a2a901485a5").addFormDataPart(
                "source", "${file.name}", requestBody
            ).addFormDataPart("format", "json").build()

        val retrofitResponse = retrofitBuilder.uploadImage(
            reqBody
        )

        retrofitResponse.enqueue(object : retrofit2.Callback<UploadResponse?> {
            override fun onResponse(
                call: retrofit2.Call<UploadResponse?>, response: retrofit2.Response<UploadResponse?>
            ) {
                Log.d(
                    TAG,
                    "onResponse: " + GsonBuilder().setPrettyPrinting().create()
                        .toJson(response.body())
                )
                val imageResponse: UploadResponse? = response.body()
                Picasso.get().load(imageResponse?.image?.url?.toUri()).into(binding.imageView)
            }

            override fun onFailure(call: retrofit2.Call<UploadResponse?>, t: Throwable) {
                Log.d(TAG, "onFailure: --> $t")
            }
        })
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
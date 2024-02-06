package com.example.retrofitmultipart


import com.example.retrofitmultipart.data.UploadResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FreeImageApiService {
    @POST("upload")
    fun uploadImage(
        @Body body: RequestBody,
    ): Call<UploadResponse>
}
package com.example.retrofitmultipart.data

data class UploadResponse(
    val image: Image,
    val status_code: Int,
    val status_txt: String,
    val success: Success
)
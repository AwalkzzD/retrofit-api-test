package com.example.retrofitmultipart.data

data class Medium(
    val bits: Int,
    val channels: Any,
    val extension: String,
    val filename: String,
    val height: Int,
    val mime: String,
    val name: String,
    val ratio: Double,
    val size: Int,
    val size_formatted: String,
    val url: String,
    val width: Int
)
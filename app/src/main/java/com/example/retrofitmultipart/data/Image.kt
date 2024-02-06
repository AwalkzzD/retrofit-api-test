package com.example.retrofitmultipart.data

data class Image(
    val bits: Int,
    val channels: Any,
    val date: String,
    val date_gmt: String,
    val description: Any,
    val display_url: String,
    val extension: String,
    val filename: String,
    val height: Int,
    val how_long_ago: String,
    val id_encoded: String,
    val md5: String,
    val medium: Medium,
    val mime: String,
    val name: String,
    val nsfw: String,
    val original_exifdata: Any,
    val original_filename: String,
    val ratio: Double,
    val size: Int,
    val size_formatted: String,
    val storage: String,
    val storage_id: Any,
    val thumb: Thumb,
    val url: String,
    val url_viewer: String,
    val views: String,
    val views_label: String,
    val width: Int
)
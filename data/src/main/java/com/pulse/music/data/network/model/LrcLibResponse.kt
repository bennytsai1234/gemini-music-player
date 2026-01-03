package com.pulse.music.data.network.model

import com.google.gson.annotations.SerializedName

data class LrcLibResponse(
    val id: Int,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("plainLyrics") val plainLyrics: String?,
    @SerializedName("syncedLyrics") val syncedLyrics: String?
)

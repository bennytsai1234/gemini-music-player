package com.pulse.music.domain.model

sealed class PlayerError(val message: String) {
    data class General(val msg: String) : PlayerError(msg)
    data class PlaybackFailed(val msg: String) : PlayerError(msg)
    data class NetworkError(val msg: String) : PlayerError(msg)
}

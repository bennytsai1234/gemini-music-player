package com.pulse.music.core.common.auth

import android.content.Intent

interface GoogleAuthProvider {
    fun getSignInIntent(): Intent
}

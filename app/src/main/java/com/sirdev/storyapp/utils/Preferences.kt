package com.sirdev.storyapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.sirdev.storyapp.BuildConfig.PREF_NAME
import com.sirdev.storyapp.data.remote.response.auth.AuthSession
import com.sirdev.storyapp.utils.Const.Companion.NAME_KEY
import com.sirdev.storyapp.utils.Const.Companion.STATE_KEY
import com.sirdev.storyapp.utils.Const.Companion.TOKEN_KEY
import com.sirdev.storyapp.utils.Const.Companion.USER_ID_KEY

class Preferences(context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()



    fun setUserLogin(auth: AuthSession) {
        editor.apply {
            putString(NAME_KEY, auth.name)
            putString(TOKEN_KEY, auth.token)
            putString(USER_ID_KEY, auth.userId)
            putBoolean(STATE_KEY, auth.isLogin)
            apply()
        }
    }

    fun logout(){
        editor.apply {
            remove(NAME_KEY)
            remove(TOKEN_KEY)
            remove(USER_ID_KEY)
            putBoolean(STATE_KEY, false)
            apply()
        }
    }

    fun getLoginData(): AuthSession {
        return AuthSession(
            pref.getString(NAME_KEY, "").toString(),
            pref.getString(TOKEN_KEY, "").toString(),
            pref.getString(USER_ID_KEY, "").toString(),
            pref.getBoolean(STATE_KEY, false)
        )
    }

}
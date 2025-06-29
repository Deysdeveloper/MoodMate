package com.example.moodmate

import android.content.Context
import android.widget.Toast

object AppUtil {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
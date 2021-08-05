package com.example.networktv.utils

import android.util.Log

open class Logger {

    companion object {

        private const val TAG = "NetworkTV"

        fun verbose(message: String) {
            Log.v(TAG, message)
        }

        fun error(message: String) {
            Log.e(TAG, message)
        }

        fun warning(message: String) {
            Log.w(TAG, message)
        }

        fun debug(message: String) {
            Log.d(TAG, message)
        }

        fun info(message: String) {
            Log.i(TAG, message)
        }
    }

}
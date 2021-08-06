package com.example.networktv.ui.main.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.networktv.R
import com.example.networktv.utils.Constants.Companion.DIALOG_STATE_KEY

class MainActivity : AppCompatActivity() {

    private var isDialogShown = false // for test

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isDialogShown = savedInstanceState?.getBoolean(DIALOG_STATE_KEY) == true

        showNetworkFragment()
    }

    private fun showNetworkFragment() {
        if (isDialogShown) { return }
        NetworkTVFragment().show(supportFragmentManager, NetworkTVFragment().javaClass.name)
        isDialogShown = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(DIALOG_STATE_KEY, isDialogShown)
    }
}
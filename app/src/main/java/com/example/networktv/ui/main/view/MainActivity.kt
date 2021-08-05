package com.example.networktv.ui.main.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.networktv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.testButton.setOnClickListener {
            val networkTVFragment = NetworkTVFragment()
            networkTVFragment.show(supportFragmentManager, NetworkTVFragment().javaClass.name)
        }
    }
}
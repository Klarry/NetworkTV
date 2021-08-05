package com.example.networktv.data.service

class NSDHelper(private val nsdService: NSDService) {
    fun getState() = nsdService.getState()
}
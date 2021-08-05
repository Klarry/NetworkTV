package com.example.networktv.data.service

import com.example.networktv.data.model.TV

data class NSDState(
    val tvList: List<TV>?,
    val event: NSDEvent
)
package com.example.networktv.data.service

import io.reactivex.Observable

interface NSDService {
    fun getState(): Observable<NSDState>
}
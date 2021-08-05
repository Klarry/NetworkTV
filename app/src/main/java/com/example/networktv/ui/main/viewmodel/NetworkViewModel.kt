package com.example.networktv.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.networktv.data.repository.TVNetworkRepository
import com.example.networktv.data.service.NSDState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class NetworkViewModel(private val tvNetworkRepository: TVNetworkRepository) : ViewModel() {
    private val nsdState = MutableLiveData<NSDState>()
    private val compositeDisposable = CompositeDisposable()

    init {
        subscribeOnTVList()
    }

    private fun subscribeOnTVList() {
        compositeDisposable.add(
            tvNetworkRepository.getState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { nsdState.postValue(it) }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getState(): LiveData<NSDState> {
        return nsdState
    }
}
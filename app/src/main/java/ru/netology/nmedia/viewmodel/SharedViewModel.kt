package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel : ViewModel() {
    val selected = MutableLiveData(false)

    fun select() {
        selected.value = true
    }
}
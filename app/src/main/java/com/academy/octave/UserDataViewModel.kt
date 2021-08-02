package com.academy.octave

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserDataViewModel : ViewModel() {
    private val selectedItem = MutableLiveData<UserData>()
    fun selectItem(item: UserData) {
        selectedItem.value = item
    }

    fun getSelectedItem(): LiveData<UserData> {
        return selectedItem
    }

    private val emailPass = MutableLiveData<Array<String?>>()
    fun selectEmailPass(item: Array<String?>) {
        emailPass.value = item
    }

    fun getEmailPass(): LiveData<Array<String?>> {
        return emailPass
    }
}
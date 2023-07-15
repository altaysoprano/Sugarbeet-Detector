package com.example.aifarmingapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aifarmingapp.data.repository.Repository
import com.example.aifarmingapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _signUpState = MutableLiveData<UiState<String>>()
    val signUpState: LiveData<UiState<String>>
        get() = _signUpState

    fun signUp(email: String, password: String) {
        _signUpState.value = UiState.Loading
        repository.signUp(email, password) {
            _signUpState.value = it
        }
    }
}

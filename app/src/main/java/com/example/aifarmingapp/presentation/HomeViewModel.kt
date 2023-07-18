package com.example.aifarmingapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aifarmingapp.data.repository.Repository
import com.example.aifarmingapp.presentation.ui.FragmentNavigation
import com.example.aifarmingapp.presentation.ui.LoginFragment
import com.example.aifarmingapp.util.UiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var navRegister: FragmentNavigation
    private val _sugarbeetCountState = MutableLiveData<UiState<Int>>()
    val sugarBeetCountState: LiveData<UiState<Int>>
        get() = _sugarbeetCountState

    init {
        getSugarBeetCount()
    }

    fun signOut(activity: FragmentNavigation) {
        navRegister = activity as FragmentNavigation
        firebaseAuth.signOut()
        navRegister.navigateFrag(LoginFragment(), false)
    }

    fun getSugarBeetCount() {
        _sugarbeetCountState.value = UiState.Loading
        repository.getSugarBeetCount {
            _sugarbeetCountState.value = it
        }
    }


}
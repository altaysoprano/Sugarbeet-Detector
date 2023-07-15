package com.example.aifarmingapp.presentation.ui

import androidx.fragment.app.Fragment

interface FragmentNavigation {
    fun navigateFrag(fragment: Fragment, addToStack: Boolean)
}
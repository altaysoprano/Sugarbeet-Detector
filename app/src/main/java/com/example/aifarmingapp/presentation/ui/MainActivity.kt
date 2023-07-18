package com.example.aifarmingapp.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.aifarmingapp.R
import com.example.aifarmingapp.presentation.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FragmentNavigation {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.coordinator, LoginFragment())
            .commit()
    }

    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.coordinator, fragment)

        if(addToStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

}
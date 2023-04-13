package com.team.testscanner.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.team.testscanner.R
import com.team.testscanner.ui.fragments.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mNavController: NavController
    private lateinit var navView: BottomNavigationView
    private lateinit var bottomAppBar: BottomAppBar
    private var isShowBottomNav = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.bottomNavigationView)

    }
}
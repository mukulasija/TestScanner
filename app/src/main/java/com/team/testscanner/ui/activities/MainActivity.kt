package com.team.testscanner.ui.activities

import android.R.attr.button
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
//import android.support.design.widget.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.team.testscanner.R
import com.team.testscanner.ui.fragments.AnalyticsFragment
import com.team.testscanner.ui.fragments.CreateTestIntro
import com.team.testscanner.ui.fragments.HomeFragment
import com.team.testscanner.ui.fragments.ResultsFragment


class MainActivity : AppCompatActivity() {
    private lateinit var mNavController: NavController
    private lateinit var navView: BottomNavigationView
    private lateinit var bottomAppBar: BottomAppBar
    private var isShowBottomNav = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      //  navView = findViewById(R.id.bottomNavigationView)
        val homeFragment = HomeFragment()
        val homeTransaction = supportFragmentManager.beginTransaction()
        homeTransaction.replace(R.id.my_fragment, homeFragment)
        homeTransaction.commit()
        val fabButton:FloatingActionButton=findViewById(R.id.fab)
        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Attach a listener to the bottom navigation view
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.firstFragment -> {
                    // Switch to the Home fragment
                    switchFragment(HomeFragment())
                    true
                }
                R.id.secondFragment -> {
                    // Switch to the Profile fragment
                    switchFragment(ResultsFragment())
                    true
                }
                R.id.thirdFragment -> {
                    // Switch to the Settings fragment
                    switchFragment(AnalyticsFragment())
                    true
                }
                else -> false
            }
        }

        fabButton.setOnClickListener {
            openActivity()
            //remove this function call just for testing test page
            // Create a new instance of the fragment
            val fragment = CreateTestIntro()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.my_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    private fun openActivity() {
        val intent = Intent(this,TestScreen::class.java)
        startActivity(intent)
    }

    private fun switchFragment(fragment: Fragment) {
        // Start a new transaction
        val transaction = supportFragmentManager.beginTransaction()

        // Replace the current fragment with the new fragment
        transaction.replace(R.id.my_fragment, fragment)

        // Add the transaction to the back stack
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }
}


//class MyHomeFragment : Fragment(R.layout.fragment_home) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    }
//}
package com.team.testscanner.ui

import android.R.attr.button
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


class MainActivity : AppCompatActivity() {
    private lateinit var mNavController: NavController
    private lateinit var navView: BottomNavigationView
    private lateinit var bottomAppBar: BottomAppBar
    private var isShowBottomNav = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      //  navView = findViewById(R.id.bottomNavigationView)
        val homeFragment = MyHomeFragment()
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
                    switchFragment(MyHomeFragment())
                    true
                }
                R.id.secondFragment -> {
                    // Switch to the Profile fragment
                    switchFragment(MyResultsFragment())
                    true
                }
                R.id.thirdFragment -> {
                    // Switch to the Settings fragment
                    switchFragment(MyAnalyticsFragment())
                    true
                }
                else -> false
            }
        }

        fabButton.setOnClickListener {
            // Create a new instance of the fragment
            val fragment = MyFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.my_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

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
class MyFragment : Fragment(R.layout.fragment_create_test_intro) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}

class MyResultsFragment : Fragment(R.layout.fragment_results) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}

class MyAnalyticsFragment : Fragment(R.layout.fragment_analytics) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}

class MyHomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}

//class MyHomeFragment : Fragment(R.layout.fragment_home) {
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    }
//}
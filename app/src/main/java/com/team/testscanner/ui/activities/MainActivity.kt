package com.team.testscanner.ui.activities

//import android.support.design.widget.FloatingActionButton
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team.testscanner.R
import com.team.testscanner.ui.fragments.AnalyticsFragment
import com.team.testscanner.ui.fragments.CreateClassroomTest
import com.team.testscanner.ui.fragments.CreateTestIntro
import com.team.testscanner.ui.fragments.HomeFragment
import com.team.testscanner.ui.fragments.ResultsFragment


class MainActivity : AppCompatActivity() {
    private lateinit var mNavController: NavController
    private lateinit var navView: BottomNavigationView
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var auth: FirebaseAuth
    private var isShowBottomNav = true
    private lateinit var studentId : String
    private lateinit var classroomId : String


 //   lateinit var mGoogleSignInClient: GoogleSignInClient
    // val auth is initialized by lazy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        openActivity()   //  comment down just for testing activity
      //  navView = findViewById(R.id.bottomNavigationView)
        val fragment_tag = intent.getStringExtra("fragment_tag")
        classroomId = intent.getStringExtra("classroomId").toString()
        studentId = intent.getStringExtra("studentId").toString()
        val fabButton:FloatingActionButton=findViewById(R.id.fab)
        val homeFragment = HomeFragment()
        val createClassroomTest = CreateClassroomTest()
        val homeTransaction = supportFragmentManager.beginTransaction()
        val bottomNavigationView:BottomNavigationView = findViewById(R.id.bottomNavigationView)
        homeTransaction.replace(R.id.my_fragment, homeFragment)
        homeTransaction.commit()

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
                R.id.fourFragment->{
                 //   mGoogleSignInClient.signOut()

                    auth = Firebase.auth
                    Firebase.auth.signOut()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        fabButton.setOnClickListener {
           // openActivity()
            //remove this function call just for testing test page
            // Create a new instance of the fragment
            val fragment = CreateTestIntro(classroomId,studentId)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.my_fragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    private fun openActivity() {
        val intent = Intent(this,PreviewActivity::class.java)
        startActivity(intent)
    }

    private fun switchFragment(fragment: Fragment) {
        // Start a new transaction
        val transaction = supportFragmentManager.beginTransaction()

        // Replace the current fragment with the new fragment
        transaction.replace(R.id.my_fragment, fragment)

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
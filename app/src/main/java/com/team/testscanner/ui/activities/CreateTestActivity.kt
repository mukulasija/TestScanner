package com.team.testscanner.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.team.testscanner.R
import com.team.testscanner.ui.fragments.CreateTestIntro
import com.team.testscanner.ui.fragments.HomeFragment


class CreateTestActivity : AppCompatActivity() {
    private  var studentId : String = ""
    private  var classroomId : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_test)
        classroomId = intent.getStringExtra("classroomId").toString()
        Toast.makeText(this,classroomId,Toast.LENGTH_SHORT).show()
        val createTestIntro = CreateTestIntro(classroomId,studentId)
        val homeTransaction = supportFragmentManager.beginTransaction()
        homeTransaction.replace(R.id.fl_create_test, createTestIntro)
        homeTransaction.commit()
    }
}
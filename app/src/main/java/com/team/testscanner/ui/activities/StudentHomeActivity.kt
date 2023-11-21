package com.team.testscanner.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R
import com.team.testscanner.adapters.ClassroomAdapter
import com.team.testscanner.databinding.ActivityTeacherHomeBinding
import com.team.testscanner.models.Classroom

class StudentHomeActivity : AppCompatActivity() {
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private var userEmail : String? = null
    private lateinit var binding: ActivityTeacherHomeBinding
    private var classroomList = mutableListOf<Classroom>()
    private lateinit var adapter: ClassroomAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_home)
    }
}
package com.team.testscanner.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R
import com.team.testscanner.databinding.ActivityClassroomBinding
import com.team.testscanner.models.Quiz

class ClassroomActivity : AppCompatActivity() {
    private lateinit var classroomId : String
    private lateinit var binding: ActivityClassroomBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var classroomTeacherName : String
    private lateinit var classroomTeacherEmail : String
    private lateinit var classroomName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        classroomId = intent.getStringExtra("classroomId").toString()
        db = FirebaseFirestore.getInstance()
        setupFirestore()
        binding.itemAddTest.cardAddTest.setOnClickListener {

        }
    }

    private fun setupFirestore(){
        val classroomDoc = db.collection("Classrooms").document(classroomId)
        Toast.makeText(this,classroomId.toString(),Toast.LENGTH_SHORT).show()
        classroomDoc.get()
            .addOnSuccessListener { documentSnapshot ->
                classroomTeacherName = documentSnapshot.getString("classroomTeacherName") ?: ""
                classroomName = documentSnapshot.getString("classroomName") ?: ""
                classroomTeacherEmail = documentSnapshot.getString("classroomTeacherEmail") ?: ""
                val classroomTeacherName = documentSnapshot.getString("classroomTeacherName")
                val classroomSection = documentSnapshot.getString("classroomSection")
                val classroomQuizMap = documentSnapshot.get("classroomQuizMap") as Map<String, Quiz>?
                setFields()
            }
            .addOnFailureListener {

            }
    }

    private fun setFields() {
        binding.classBanner.tvTeacherName.text = classroomTeacherName
        binding.classBanner.tvClassroomName.text = classroomName
    }
}
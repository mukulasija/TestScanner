package com.team.testscanner.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.team.testscanner.R
import com.team.testscanner.adapters.ClassroomAdapter
import com.team.testscanner.adapters.MyAdapter
import com.team.testscanner.databinding.ActivityClassroomBinding
import com.team.testscanner.models.Classroom
import com.team.testscanner.models.Quiz
import kotlinx.coroutines.tasks.await

class ClassroomActivity : AppCompatActivity() {
    private lateinit var classroomId : String
    private lateinit var studentId : String
    private lateinit var binding: ActivityClassroomBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var classroomTeacherName : String
    private lateinit var classroomTeacherEmail : String
    private lateinit var mode : String
    private var userEmail : String? = null
    private lateinit var classroomName : String
    private var quizList = mutableListOf<Quiz>()
    private lateinit var myAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        classroomId = intent.getStringExtra("classroomId").toString()
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser : FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            userEmail = currentUser.email.toString()
            Toast.makeText(this,userEmail.toString(),Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(this,userEmail.toString() + " Email not found, Please update the email id",Toast.LENGTH_LONG).show()
        }
        setupFirestore()
        binding.itemAddTest.cardAddTest.setOnClickListener {
            val intent = Intent(this,CreateTestActivity::class.java);
            intent.putExtra("fragment_tag","create_test_intro")
            intent.putExtra("classroomId",classroomId)
            startActivity(intent)
            finish()
        }

    }
    private fun setUpRecyclerView(){
        val layoutManager= LinearLayoutManager(this)
        val recyclerView: RecyclerView = binding.rvClassroomTest
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=myAdapter
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
                val classroomQuizMap = documentSnapshot.get("classroomQuizMap") as Map<String, String>?
                setFields()
                setUpQuizList(classroomQuizMap)
            }
            .addOnFailureListener {

            }
    }

    private fun setUpQuizList(classroomQuizMap: Map<String, String>?) {
        val quizIdList = classroomQuizMap!!.values?.toMutableList()
        val newQuizList : MutableList<Quiz>  = mutableListOf()
        val quizCollection = db.collection("quizzes")
        quizList.clear()
        for(quizId in quizIdList){
            Log.d("quizListId",quizId)
            quizCollection.whereEqualTo("id",quizId.trim()).addSnapshotListener { value, error ->
                if(value==null || error!=null){
                    Toast.makeText(this,"Error fetching data",Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this,"fetched data",Toast.LENGTH_SHORT).show()
                if (value != null) {
                    Log.d("quiz",value.toObjects(Quiz::class.java).toString())
                }
                quizList.addAll(value!!.toObjects(Quiz::class.java))
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setFields() {
        if(userEmail==classroomTeacherEmail){
            mode = "teacher"
        }
        else{
            mode = "student"
        }
        myAdapter = MyAdapter(this,quizList,mode)
        setUpRecyclerView()
        binding.classBanner.tvTeacherName.text = classroomTeacherName
        binding.classBanner.tvClassroomName.text = classroomName
    }
}
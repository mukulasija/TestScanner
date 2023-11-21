package com.team.testscanner.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.adapters.ClassroomAdapter
import com.team.testscanner.databinding.ActivityTeacherHomeBinding
import com.team.testscanner.models.Classroom
import com.team.testscanner.models.Quiz

class TeacherHomeActivity : AppCompatActivity() {
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private var userEmail : String? = null
    private lateinit var binding: ActivityTeacherHomeBinding
    private var classroomList = mutableListOf<Classroom>()
    private var classroomIdList = mutableListOf<String>()
    private lateinit var adapter: ClassroomAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvActivityTeacher
        binding.fabAddClassroom.setOnClickListener {
            val createClassroomIntent = Intent(this,CreateClassroomActivity::class.java)
            startActivity(createClassroomIntent)
        }
        adapter = ClassroomAdapter(this,classroomList)
        setUpRecyclerView()
        auth = FirebaseAuth.getInstance()
        val currentUser : FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            userEmail = currentUser.email.toString()
            Toast.makeText(this,userEmail.toString(),Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(this,userEmail.toString() + " Email not found, Please update the email id",Toast.LENGTH_LONG).show()
        }
//        setUpFireStore()
        fetchDataFromFirestore()
//        addClassroomToFirestore(newclassroom,quizList)
    }
    private fun setUpRecyclerView(){
        val layoutManager= LinearLayoutManager(this)
        val recyclerView: RecyclerView = binding.rvActivityTeacher
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=adapter
    }
    private fun showProgressBar(bol : Boolean){
        if(bol){
            binding.idPBLoading.visibility=View.VISIBLE
            binding.rvActivityTeacher.visibility=View.GONE
        }
        else{
            binding.idPBLoading.visibility=View.GONE
            binding.rvActivityTeacher.visibility=View.VISIBLE
        }
    }
    private fun hideProgressBar(){
        binding.idPBLoading.visibility=View.GONE
        binding.rvActivityTeacher.visibility=View.VISIBLE
    }
    fun fetchDataFromFirestore() {
        firestore = FirebaseFirestore.getInstance()
        val enrollmentsCollection = firestore.collection("Enrollments")
        classroomIdList.clear()
//        Toast.makeText(this,"sample",Toast.LENGTH_LONG).show()
        enrollmentsCollection.whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
//                    Toast.makeText(this,querySnapshot.toString(),Toast.LENGTH_LONG).show()
                    val document = querySnapshot.documents[0] // Assuming only one document is retrieved
                    val classroomArray = document.get("classrooms") as? List<String>
                    classroomArray?.forEach { classroomId ->
                        // Assuming 'classroomId' is the ID of a classroom document
                        // Fetch the individual classroom data or construct a Classroom object
                        // Add the classroom to the list
                        classroomIdList.add(classroomId)
//                        Toast.makeText(this,classroomId.toString(),Toast.LENGTH_LONG).show()

                    }
                    searchClassrooms()
                    // Do something with the updated classroomList
                    // (e.g., notify an adapter in an Android app)
                    // adapter.notifyDataSetChanged()
                } else {
                    // Document with the given emailId not found
                    // Handle this case
                }
            }
            .addOnFailureListener { exception ->
                // Handle exceptions or errors here
            }
    }
    private fun searchClassrooms(){
        firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("Classrooms")
        val userEmailId : String = userEmail.toString()
        Log.d("email",userEmail.toString().trim())
        for(classroomId in classroomIdList){
            collectionReference.whereEqualTo("classroomId",classroomId.trim()).addSnapshotListener { value, error ->
                if(value==null || error!=null){
                    Toast.makeText(this,"Error fetching data",Toast.LENGTH_SHORT).show()
                }
                classroomList.clear()
                classroomList.addAll(value!!.toObjects(Classroom::class.java))
                adapter.notifyDataSetChanged()
                hideProgressBar()
            }
        }
    }
    private fun setUpFireStore() {
        firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("Classrooms")
        val userEmailId : String = userEmail.toString()
        Log.d("email",userEmail.toString().trim())
        collectionReference.whereEqualTo("classroomTeacherEmail",userEmailId.trim()).addSnapshotListener { value, error ->
            if(value==null || error!=null){
                Toast.makeText(this,"Error fetching data",Toast.LENGTH_SHORT).show()
            }
            classroomList.clear()
            classroomList.addAll(value!!.toObjects(Classroom::class.java))
            adapter.notifyDataSetChanged()
            hideProgressBar()
        }
//        val teacherId = "mukulasija123@gmail.com"

//        collectionReference.addSnapshotListener { value, error ->
//            if(value == null || error != null){
//                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
//                return@addSnapshotListener
//            }
//            //   Log.d("DATA", value.toObjects(Quiz::class.java).toString())
//            classroomList.clear()
//            classroomList.addAll(value.toObjects(Classroom::class.java))
//            adapter.notifyDataSetChanged()
//            hideProgressBar()
//        }
    }



}
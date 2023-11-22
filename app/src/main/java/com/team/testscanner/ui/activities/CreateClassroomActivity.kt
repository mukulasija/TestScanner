package com.team.testscanner.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.databinding.ActivityCreateClassroomBinding
import com.team.testscanner.models.Classroom
import com.team.testscanner.models.Quiz
import com.team.testscanner.utility.MyUtility

class CreateClassroomActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateClassroomBinding
    private lateinit var teacherEmail : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClassroomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUserCredentials()
        binding.btnCreateClassroom.setOnClickListener {
            createClassroomFromFields()
        }

    }

    private fun getUserCredentials() {
        val auth : FirebaseAuth = FirebaseAuth.getInstance()
        teacherEmail = auth.currentUser!!.email.toString()
    }

    private fun createClassroomFromFields(){
        if(MyUtility.areEditTextsNotEmpty(binding.etClassName, binding.etClassSection, binding.etClassTeacherName)==false){
            return
        }
        val newClassroom = Classroom()
        newClassroom.classroomName = binding.etClassName.text.toString()
        newClassroom.classroomTeacherEmail = teacherEmail
        newClassroom.classroomTeacherName = binding.etClassTeacherName.text.toString()
        newClassroom.classroomSection = binding.etClassSection.text.toString()
        newClassroom.classroomQuizMap = mutableMapOf()
        addClassroomToFirestore(newClassroom)
    }

    private fun addClassroomToFirestore(classroom: Classroom) {
        val classroomsRef = FirebaseFirestore.getInstance().collection("Classrooms")
        val newClassroomId = classroomsRef.document().id
        classroom.classroomId = newClassroomId

        classroomsRef.document(newClassroomId)
            .set(classroom)
            .addOnSuccessListener {
                Toast.makeText(this, "Classroom added successfully", Toast.LENGTH_SHORT).show()
                addTeacherEnrollment(newClassroomId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error creating classroom", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addTeacherEnrollment(classroomId : String) {
        var enrollmentCollection = FirebaseFirestore.getInstance().collection("Enrollments")
        enrollmentCollection.whereEqualTo("email",teacherEmail)
            .get()
            .addOnSuccessListener {querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No document found, create a new one
                    val newDocument = hashMapOf(
                        "email" to teacherEmail,
                        "classrooms" to mutableListOf(classroomId)
                    )

                    // Add the new document to Firestore
                    enrollmentCollection
                        .add(newDocument)
                        .addOnSuccessListener { documentReference ->
                            // New document added successfully
                            callOnClassroomCreated()
                            // Handle success if needed
                        }
                        .addOnFailureListener { e ->
                            // Handle failures
                        }
                }
                else{
                    var document = querySnapshot.documents[0]
                    val classrooms = document.get("classrooms") as MutableList<String>?
                    classrooms!!.add(classroomId)
                    document.reference.update("classrooms",classrooms)
                        .addOnSuccessListener {
                            callOnClassroomCreated()
                        }
                        .addOnFailureListener {

                        }
                }
            }
            .addOnFailureListener {

            }
    }

    private fun callOnClassroomCreated() {
        Toast.makeText(this,"Classroom Created Successfully",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,TeacherHomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

//    private fun addClassroomToFirestore(classroom: Classroom, quizList: MutableMap<String, Quiz>) {
////        quiz.questions= questions
//        classroom.quizList=quizList
//        val collectionRef = FirebaseFirestore.getInstance().collection("Classrooms")
//        collectionRef.get().addOnSuccessListener { querySnapshot ->
//            val numDocuments = querySnapshot.size()
//            val newDocumentNumber = numDocuments + 1
//            classroom.classroomId = "classroom$newDocumentNumber"
//            val newClassroomRef = collectionRef.document("classroom$newDocumentNumber")
//            newClassroomRef.set(classroom)
//                .addOnSuccessListener {
//                    Toast.makeText(this,"Classroom Created Successfully", Toast.LENGTH_SHORT).show()
////                    showProgressBar(false)
////                    val intent = Intent(context,MainActivity::class.java)
////                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////                    startActivity(intent)
//                }.addOnFailureListener{
////                    showProgressBar(false)
//                    Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
//                }
//        }.addOnFailureListener { exception ->
//            println("Error getting number of documents in collection: $exception")
//        }
//    }
}
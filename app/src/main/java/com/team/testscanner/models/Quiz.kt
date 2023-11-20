package com.team.testscanner.models

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Duration

data class Quiz (
    var id : String = "",
    var title: String = "",
    var isAttempted : Boolean = false,
    var questions: MutableMap<String, Question> = mutableMapOf(),
    var marksPerQuestion : Int =1,
    var score : Int =0,
    var isKeyAvailable : Boolean = false,
    var duration : Long = 3600,
    var imMap : MutableMap<String,String> = mutableMapOf()
) {
    fun addQuiztoClassroom(context : Context, classroomId : String, db : FirebaseFirestore){
        val quizCollecetionRef = db.collection("Classroom/$classroomId/Quizzes")
        quizCollecetionRef.add(this).addOnSuccessListener {
            val quizId = it.id
        }.addOnFailureListener {
            Toast.makeText(context,"Error Adding Quiz Please try again later",Toast.LENGTH_SHORT).show()
        }
    }
    constructor(id: String, title: String, questions: MutableMap<String, Question>) : this(
        id,
        title,
         false,
        questions = questions,

    )
    constructor(id : String,title: String,questions: MutableMap<String, Question>,isAttempted: Boolean,marksPerQuestion: Int,score: Int,isKeyAvailable: Boolean) :
            this(id,title,isAttempted,questions,marksPerQuestion,score,isKeyAvailable)
}

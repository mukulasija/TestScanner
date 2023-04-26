package com.team.testscanner.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.team.testscanner.R
import com.team.testscanner.adapters.CheckBoxAdapter
import com.team.testscanner.adapters.MyAdapter
import com.team.testscanner.models.HighStart
import com.team.testscanner.models.MyMap
import com.team.testscanner.models.Question
import com.team.testscanner.models.Quiz
import com.team.testscanner.other.ResponseManipulator
import com.team.testscanner.ui.fragments.addAllQuestions

class PreviewActivity : AppCompatActivity() {
//    lateinit var selectedLanguage: BooleanArray
//    var langArray = arrayOf("Java", "C++", "Kotlin", "C", "Python", "Javascript")
    lateinit var textView : TextView
    val highMap = MyMap.myMap.toMutableMap()
    val questions_start : MutableList<ResponseManipulator.Start> = mutableListOf()
    val questions_end : MutableList<ResponseManipulator.Start> = mutableListOf()
    var questionlist = mutableListOf<Question>()
    var questions : MutableMap<String,Question> = mutableMapOf()
    lateinit var base64ImageString : String
    lateinit var quiz : Quiz
    private lateinit var adapter: CheckBoxAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        textView = findViewById(R.id.tv_preview);
        MyMap.myMap.clear()
        val prevBtn : Button = findViewById(R.id.btnPrev)
        prevBtn.setOnClickListener {
            process()
        }
        val quizTitle = intent.getStringExtra("quizTitle")
        val quizDuration = intent.getLongExtra("quizDuration",0L)
        quiz = Quiz()
        quiz.title = quizTitle!!
        quiz.duration = quizDuration
        if(highMap.size>0){
            val list : MutableList<HighStart> = highMap.entries.first().value
            adapter = CheckBoxAdapter(this,list)
            setUpRecyclerView()
        }
    }

    private fun process() {
        var index = 0
        val start = highMap.entries.first().value
        base64ImageString = highMap.entries.first().key
        for(value in start){
//            if(value.description[0].isDigit()==true && value.description.length==1){
//                if(value.description[0].isDigit() && value.description.length==2 && value.description[1]=='.' ){
//            if(value.description[0]=='Q'){
            if(value.tick==true){
//                hstartList.add(HighStart(value.description,value.ytop,value.ydown,true))
                if(questions_start.size>0){
                    val pval = start[index-1]
                    questions_end.add(ResponseManipulator.Start(pval.description,pval.ytop,pval.ydown))
                }
                questions_start.add(ResponseManipulator.Start(value.description,value.ytop,value.ydown))
            }
            index=index+1
        }
        val pval = start[index-1]
        questions_end.add(ResponseManipulator.Start(pval.description,pval.ytop,pval.ydown))
        questionlist = getQuestionList()
//        questionlist.addAll(getQuestionList())
        questions.addAllQuestions(questionlist)
        quiz.questions = questions
        addQuizToFireStore(quiz)
    }

    private fun addQuizToFireStore(quiz: Quiz) {
        quiz.questions= questions
        val collectionRef = FirebaseFirestore.getInstance().collection("quizzes")
        collectionRef.get().addOnSuccessListener { querySnapshot ->
            val numDocuments = querySnapshot.size()
            val newDocumentNumber = numDocuments + 1
            quiz.id = "quiz$newDocumentNumber"
            val newQuizRef = collectionRef.document("quiz$newDocumentNumber")
            newQuizRef.set(quiz)
                .addOnSuccessListener {
                    Toast.makeText(this,"Test Created Successfully", Toast.LENGTH_SHORT).show()
//                    showProgressBar(false)
                    val intent = Intent(this,MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }.addOnFailureListener{
//                    showProgressBar(false)
                    Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { exception ->
            println("Error getting number of documents in collection: $exception")
        }
    }


    private fun getQuestionList() : MutableList<Question>{
        var index = 0
        if(questions_start.size==0){
            return questionlist
        }
        while (index<questions_start.size){
            questionlist.add(Question(imageUrl = base64ImageString, ytop = questions_start[index].ytop, yend = questions_end[index].ydown))
            index=index+1
        }
        return questionlist
    }

    private fun setUpRecyclerView(){
        val layoutManager= LinearLayoutManager(this)
        val recyclerView: RecyclerView = findViewById(R.id.prev_recycler)
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=adapter
    }

}
package com.team.testscanner.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R
import com.team.testscanner.adapters.CheckBoxAdapter
import com.team.testscanner.models.HighStart
import com.team.testscanner.models.MyMap
import com.team.testscanner.models.Question
import com.team.testscanner.models.Quiz
import com.team.testscanner.other.ResponseManipulator
import com.team.testscanner.ui.fragments.addAllQuestions

class PreviewActivity : AppCompatActivity() {
    lateinit var textView : TextView
    var questions : MutableMap<String,Question> = mutableMapOf()
    lateinit var base64ImageString : String
    lateinit var start: MutableList<HighStart>
    lateinit var quiz : Quiz
    private lateinit var adapter: CheckBoxAdapter
    private var classroomId : String = ""
    private  var studentId : String = ""
    private lateinit var prevBtn : Button
    private lateinit var highMap : MutableMap<String,MutableList<HighStart>>
    private var imMap : MutableMap<Int,String> = mutableMapOf()
    private var imgIndex : String = ""
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        highMap = MyMap.myMap.toMutableMap()
        Log.d("highMap",MyMap.myMap.size.toString())
        textView = findViewById(R.id.tv_preview);
        MyMap.myMap.clear()
        prevBtn= findViewById(R.id.btnPrev)
        val quizTitle = intent.getStringExtra("quizTitle")
        val quizDuration = intent.getLongExtra("quizDuration",0L)
        classroomId = intent.getStringExtra("classroomId").toString()
        studentId = intent.getStringExtra("studentId").toString()
        val mode = intent.getIntExtra("previewMode",1)
        quiz = Quiz()
        quiz.title = quizTitle!!
        quiz.duration = quizDuration
        if(mode==0){
           recyclerView= findViewById(R.id.prev_recycler)
            showProgressBar(true)
        }
        bindViews()
        prevBtn.setOnClickListener {
            process()
            bindViews()
        }
        if(mode==0){
            val n = highMap.size
            for(i in 0..n){
                prevBtn.callOnClick()
            }
        }
    }
    private fun showProgressBar(bol : Boolean){
        if(bol){
            recyclerView.visibility= View.GONE
            prevBtn.visibility=View.GONE
//            loadingPB.visibility= View.VISIBLE
        }
        else{
            recyclerView.visibility= View.VISIBLE
            prevBtn.visibility=View.VISIBLE
//            loadingPB.visibility= View.GONE
        }
    }

    private fun bindViews() {
        if(highMap.size==0){
            prevBtn.isClickable=false
            addQuizToFireStore(quiz)
            return
        }
        if(highMap.size==1){
            prevBtn.text="Generate Test"
        }
        else{
            prevBtn.text="Next"
        }
//        start = highMap.entries.first().value
//        base64ImageString = highMap.entries.first().key
//        val firstKey = highMap.keys.first()
        val firstKey = highMap.keys.first()
        val firstValue = highMap[firstKey]!!
        highMap.remove(firstKey)
        start = firstValue
        base64ImageString = firstKey
        imgIndex = imMap.addData(base64ImageString).toString()
//        println("First value: $firstValue")
//        highMap.remove(firstKey)
        adapter = CheckBoxAdapter(this,start)
        setUpRecyclerView()
    }
    private fun process() {
        val questions_start : MutableList<ResponseManipulator.Start> = mutableListOf()
        val questions_end : MutableList<ResponseManipulator.Start> = mutableListOf()
        var index = 0
//        questions_start.clear()
//        questions_end.clear()
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
        val questionlist = getQuestionList(questions_start,questions_end)
//        questionlist.addAll(getQuestionList())
        questions.addAllQuestions(questionlist)
        quiz.questions = questions
    }

    private fun addQuizToFireStore(quiz: Quiz) {
        quiz.questions= questions
        val stringKeyedMap = imMap.toMapWithStrKeys()
        Log.d("imageMap",stringKeyedMap.size.toString())
        quiz.imMap = stringKeyedMap
        val collectionRef = FirebaseFirestore.getInstance().collection("quizzes")
        collectionRef.get().addOnSuccessListener { querySnapshot ->
            val numDocuments = querySnapshot.size()
            val newDocumentNumber = numDocuments + 1
            quiz.id = "quiz$newDocumentNumber"
            val newQuizRef = collectionRef.document("quiz$newDocumentNumber")
            newQuizRef.set(quiz)
                .addOnSuccessListener {
                    Toast.makeText(this,"Test Created Successfully", Toast.LENGTH_SHORT).show()
                    if(classroomId.length>0){
                        Toast.makeText(this,"add to classroom", Toast.LENGTH_SHORT).show()
                        addNewQuizToClassroom(classroomId,quiz.id)
                    }
                    if(studentId.length>0){

                    }
//                    showProgressBar(false)
                    var intent = Intent(this,MainActivity::class.java)
                    if(classroomId.length>0){
                        intent = Intent(this,ClassroomActivity::class.java)
                        intent.putExtra("classroomId",classroomId)
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }.addOnFailureListener{
//                    showProgressBar(false)
                    prevBtn.isClickable=true
                    Toast.makeText(this,it.toString(), Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { exception ->
            println("Error getting number of documents in collection: $exception")
        }
    }
    fun addNewQuizToClassroom(classroomId: String, newQuizId : String) {
        val classroomsCollection = FirebaseFirestore.getInstance().collection("Classrooms")
        val specificClassroomDoc = classroomsCollection.document(classroomId)

        specificClassroomDoc.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val classroomData = documentSnapshot.data
                    val classroomQuizMap = classroomData?.get("classroomQuizMap") as? MutableMap<String, Any>

                    // Add a new quiz to the classroomQuizMap
                    classroomQuizMap?.let {
                        val quizCount = "quiz_${classroomQuizMap.size + 1}" // Generate a new quiz ID
                        it[quizCount] = newQuizId

                        // Update the classroom document with the updated classroomQuizMap
                        specificClassroomDoc.update("classroomQuizMap", classroomQuizMap)
                            .addOnSuccessListener {
                                println("New quiz added successfully to classroom with ID: $classroomId")
                            }
                            .addOnFailureListener { exception ->
                                println("Error updating classroom document: $exception")
                            }
                    }
                } else {
                    println("Classroom document with ID: $classroomId does not exist")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting classroom document: $exception")
            }
    }

    private fun getQuestionList(
        questions_start: MutableList<ResponseManipulator.Start>,
        questions_end: MutableList<ResponseManipulator.Start>
    ): MutableList<Question>{
        var questionlist = mutableListOf<Question>()
        var index = 0
        if(questions_start.size==0){
            return questionlist
        }
        while (index<questions_start.size){
            questionlist.add(Question(imageUrl = imgIndex, ytop = questions_start[index].ytop, yend = questions_end[index].ydown))
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
    // Define the extension function on MutableMap<Int, String>
    fun MutableMap<Int, String>.addData(data: String)  : Int{
        // Get the current max key and increment it by 1
        val key = this.keys.maxOrNull()?.plus(1) ?: 1
        // Add the new data with the new key
        this[key] = data
        return key
    }
    fun <T> Map<Int, T>.toMapWithStrKeys(): MutableMap<String, T> {
        val result = mutableMapOf<String, T>()
        for ((key, value) in this) {
            result[key.toString()] = value
        }
        return result
    }


}
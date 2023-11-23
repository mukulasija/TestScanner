package com.team.testscanner.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.team.testscanner.R
import com.team.testscanner.adapters.OptionAdapter
import com.team.testscanner.models.Attempt
import com.team.testscanner.models.OptionSelector
import com.team.testscanner.models.Question
import com.team.testscanner.models.Quiz

class AnalysisActivity : AppCompatActivity() {
    lateinit var firestore: FirebaseFirestore
    var quizzes : MutableList<Quiz>? = null
    var questions: MutableMap<String, Question>? = null
    var index = 1
    lateinit var btnNext : Button
    lateinit var btnPrevious : Button
    private lateinit var attempt: Attempt
    lateinit var btnSubmit : Button
    lateinit var questionImageView : ImageView
    private lateinit var loadingPB: ProgressBar
    var mode : Int = 0
//    val optionSelector = OptionSelector()
    lateinit var optionSelectorList : MutableList<OptionSelector>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)
        btnPrevious = findViewById<Button>(R.id.btnPreviousAk)
        btnNext = findViewById<Button>(R.id.btnNextAk)
        btnSubmit = findViewById<Button>(R.id.btnSubmitAk)
        questionImageView = findViewById(R.id.question_ImageAk)
        loadingPB = findViewById(R.id.idPBAnalLoading)
        loadingPB.visibility= View.VISIBLE
        questionImageView.visibility=View.GONE
        val md = intent.getStringExtra("mode")
        if(md=="update"){
            mode=0
        }
        else{
            mode=1
            attempt = intent.getSerializableExtra("attemptObject") as Attempt
        }
//        setupQuiz()
        setUpFirestore()
        setUpEventListener()
    }

    private fun setupQuiz() {
        val quizData = intent.getStringExtra("QUIZ")
        val quiz = Gson().fromJson<Quiz>(quizData, Quiz::class.java)
        quizzes = mutableListOf(quiz)
        questions = quizzes!![0].questions
        optionSelectorList = MutableList(questions!!.size) { OptionSelector() }
    }

    private fun setUpEventListener() {

        btnPrevious.setOnClickListener {
            index--
            bindViews()
        }

        btnNext.setOnClickListener {
            index++
            bindViews()
        }

        btnSubmit.setOnClickListener {
            if(mode==1){
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finishAffinity()
                onBackPressed()
                finish()
                return@setOnClickListener
            }else{
                showProgressBar(true)
                updateAnswerKey()
                calculateScore()
                addAnswerKeyToFirebase(quizzes!![0])
            }
        }
    }
    private fun addAnswerKeyToFirebase(quiz : Quiz) {
        val collectionRef = FirebaseFirestore.getInstance().collection("quizzes")
        collectionRef.document(quiz.id).set(quiz)
            .addOnSuccessListener {
                showProgressBar(false)
                Toast.makeText(this,"answer key updated successfully",Toast.LENGTH_SHORT).show()
                calculateAttemptScore(quiz)
                onBackPressed()
                finish()
                return@addOnSuccessListener
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()

            }.addOnFailureListener {
                Toast.makeText(this,"Some Error Occurred",Toast.LENGTH_SHORT).show()
            }
    }
    private fun calculateScore() : Int{
        var score = 0
        for (entry in quizzes!![0].questions.entries) {
            val question = entry.value
            if (question.answer == question.userAnswer) {
                score += quizzes!![0].marksPerQuestion
            }
        }
//        val txtScore = findViewById<TextView>(R.id.test_score)
//        txtScore.text = "Your Score : $score"
//        quizzes!![0].score=score
        return score
    }
    private fun calculateAttemptScore(attempt : Attempt,quiz : Quiz): Int {
        var score = 0
        var index = 1
        while(index <= quiz.questions!!.size){
            if(attempt.selectedOptions[index-1]==quiz.questions["$index"]!!.answer){
                score=score+1
            }
//            questions!!["$index"]!!.answer= optionSelectorList[index-1].userAnswer
            index=index+1
        }
        return score
    }
//    private fun testfunction(quiz: Quiz){
//        var quizAttemptsCollection = FirebaseFirestore.getInstance().collection("quizAttempts")
//        var attemptList = mutableListOf<Attempt>()
//        attemptList.clear()
//        quizAttemptsCollection.whereEqualTo("quizId",quiz.id)
//            .get()
//            .addOnSuccessListener {
//                val documents = it.documents
//                if(documents.isEmpty()){
//                    return@addOnSuccessListener
//                }
//                for(document in documents){
//                    val attemptMap = document.get("attemptList") as List<MutableMap<String,Any>>
//                    for(item in attemptMap){
//                        val selectedOptions = item.getValue("selectedOptions") as List<String>
//                        val score = item.getValue("score").toString()
//                        val studentId = item.getValue("studentId").toString()
//                        val studentName = item.getValue("studentName").toString()
//                        val newAttempt : Attempt = Attempt(score=score.toInt(), studentId = studentId, studentName = studentName, selectedOptions = selectedOptions)
//                        attemptList.add(newAttempt)
//                    }
//                }
//            }
//    }
    private fun calculateAttemptScore(quiz: Quiz){
        var enrollmentCollection = FirebaseFirestore.getInstance().collection("quizAttempts")
        enrollmentCollection.whereEqualTo("quizId",quiz.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isEmpty()) {
                    return@addOnSuccessListener
                }
//                    var document = querySnapshot.documents[0]
                val documents = querySnapshot.documents
                val attemptList = mutableListOf<Attempt>()
                for (document in documents) {
                    val attemptMap = document.get("attemptList") as List<MutableMap<String, Any>>
                    for (item in attemptMap) {
                        val selectedOptions = item.getValue("selectedOptions") as List<String>
                        val score = item.getValue("score").toString()
                        val studentId = item.getValue("studentId").toString()
                        val studentName = item.getValue("studentName").toString()
                        val newAttempt: Attempt = Attempt(
                            score = score.toInt(),
                            studentId = studentId,
                            studentName = studentName,
                            selectedOptions = selectedOptions
                        )
                        attemptList.add(newAttempt)
                    }
//                    val attemptList = document.get("attemptList") as MutableList<Attempt>
                    for (attempt in attemptList) {
                        var score = 0
                        var index = 1
                        while (index <= quiz.questions!!.size) {
                            if (attempt.selectedOptions[index - 1] == quiz.questions["$index"]!!.answer) {
                                score = score + 1
                            }
//            questions!!["$index"]!!.answer= optionSelectorList[index-1].userAnswer
                            index = index + 1
                        }
                        attempt.score = score
//                         calculateAttemptScore(attempt, quiz)
                    }
                    document.reference.update("attemptList", attemptList)
                        .addOnSuccessListener {
                            Toast.makeText(this, "updated Successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {

                        }
                }
            }
            .addOnFailureListener {

            }
    }
    private fun updateAnswerKey() {
        var index = 1
        while(index <= questions!!.size){
            questions!!["$index"]!!.answer= optionSelectorList[index-1].userAnswer
            index=index+1
        }
        quizzes!![0].isKeyAvailable=true
    }
    private fun showProgressBar(bol : Boolean){
        if(bol){
            questionImageView.visibility=View.GONE
            loadingPB.visibility=View.VISIBLE
        }
        else{
            questionImageView.visibility=View.VISIBLE
            loadingPB.visibility=View.GONE
        }
    }
    private fun bindViews() {
        //yet to implement firebase database and delete this dummydatafunction
        btnPrevious.visibility = View.GONE
        btnNext.visibility = View.GONE
        showProgressBar(true)
        if(index == 1){ //first question
            btnNext.visibility = View.VISIBLE
        }
        else if(index == questions!!.size) { // last question
            btnPrevious.visibility = View.VISIBLE
        }
        else{ // Middle
            btnPrevious.visibility = View.VISIBLE
            btnNext.visibility = View.VISIBLE
        }
        val optionList = findViewById<RecyclerView>(R.id.optionListAk)
        if(questions!!.size==0){
            Toast.makeText(this,"No questions in the Test", Toast.LENGTH_SHORT).show()
            return
        }
        val question = questions!!["$index"]
        val optionSelector = optionSelectorList[index-1]
        val imageMap = quizzes!![0].imMap
        question?.let {
//            setImageWithData(it.imageUrl,it.ytop,it.yend);
            loadBase64ImageWithUrl(imageMap[it.imageUrl],it.ytop,it.yend)
            val optionAdapter = OptionAdapter(this, optionSelector,it,mode)
            optionList.layoutManager = LinearLayoutManager(this)
            optionList.adapter = optionAdapter
            optionList.setHasFixedSize(true)
        }
    }
    fun loadBase64ImageWithUrl(base64Image: String?, ytop: Int, yend: Int) {
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            0, ytop,
            bitmap.width,
            yend - ytop
        )

        Glide.with(questionImageView)
            .load(croppedBitmap)
            .into(questionImageView)
        showProgressBar(false)
    }
    private fun setImageWithData(imageUrl: String, ytop: Int, yend: Int) {
//        Glide.with(this)
//            .load(imageUrl)
//            .into(imageView)
//        return

//
        // Load the image using Glide
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    // Get the cropped portion of the image
                    val croppedBitmap = Bitmap.createBitmap(
                        resource,
                        0,          // x-coordinate of the top-left corner of the cropped area
                        ytop,       // y-coordinate of the top-left corner of the cropped area
                        resource.width, // width of the cropped area
                        yend - ytop // height of the cropped area
                    )

                    // Set the cropped bitmap to the ImageView
                    val croppedImageView = findViewById<ImageView>(R.id.question_ImageAk)
                    croppedImageView.setImageBitmap(croppedBitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Not implemented
                }
            })
    }
    private fun setUpFirestore() {
        firestore = FirebaseFirestore.getInstance()
        var id = intent.getStringExtra("id")
        if (id != null) {
            firestore.collection("quizzes").whereEqualTo("id",id)
                .get()
                .addOnSuccessListener {
                    if(it != null && !it.isEmpty){
                        quizzes = it.toObjects(Quiz::class.java)
                        questions = quizzes!![0].questions
                        if(mode==1){
                            var index = 1;
                            while(index<=attempt.selectedOptions!!.size){
                                val userAnswer = attempt.selectedOptions[index-1]
                                questions!!["$index"]!!.userAnswer = userAnswer
                                index += 1
                            }
                        }
                        optionSelectorList = MutableList(questions!!.size) { OptionSelector() }
                        bindViews()
                    }
                }
        }
    }
}
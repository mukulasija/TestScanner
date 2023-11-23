package com.team.testscanner.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R
import com.team.testscanner.adapters.OptionAdapter
import com.team.testscanner.models.Attempt
import com.team.testscanner.models.OptionSelector
import com.team.testscanner.models.Question
import com.team.testscanner.models.Quiz
import java.util.concurrent.TimeUnit

class TestScreen : AppCompatActivity() {
    lateinit var firestore: FirebaseFirestore
    var quizzes : MutableList<Quiz>? = null
    var questions: MutableMap<String, Question>? = null
    var index = 1
    private var studentId : String = ""
    lateinit var btnNext : Button
    lateinit var btnPrevious : Button
    lateinit var optionSelectorList : MutableList<OptionSelector>
    lateinit var btnSubmit : Button
    lateinit var questionImageView : ImageView
    lateinit var tvTimer : TextView
    lateinit var timer : CountDownTimer
    private lateinit var mode : String
    lateinit var tvqNo : TextView
    private var score = 0
    private lateinit var loadingPB: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_screen)
        btnPrevious = findViewById<Button>(R.id.btnPrev)
        btnNext = findViewById<Button>(R.id.btnNext)
        btnSubmit = findViewById<Button>(R.id.btnSubmit)
        loadingPB = findViewById(R.id.idPBTestLoading)
        studentId = intent.getStringExtra("studentId").toString()
        mode = intent.getStringExtra("mode").toString()
        loadingPB.visibility= View.VISIBLE
        questionImageView = findViewById(R.id.question_Image)
        tvTimer = findViewById(R.id.tvTimer)
        tvqNo = findViewById(R.id.tvqNo)
        questionImageView.visibility=View.GONE
        setUpFirestore()
        setUpEventListener()
    }
    fun updateStudentAttempt(quizId : String){
        var selectedOptions : MutableList<String> = mutableListOf()
        var index = 1
        while(index <= questions!!.size){
            selectedOptions.add(optionSelectorList[index-1].userAnswer)
            index=index+1
        }
        quizzes!![0].isAttempted=true
        if(studentId.length==0){
            Toast.makeText(this,"can't varify student Id, try again later",Toast.LENGTH_SHORT).show()
            return
        }
        var attempt = Attempt(studentId,studentId,selectedOptions,score)
        updateStudentAttemptToFirebase(quizId,attempt)
    }
    private fun updateStudentAttemptToFirebase(quizId : String, attempt: Attempt){
            var enrollmentCollection = FirebaseFirestore.getInstance().collection("quizAttempts")
            enrollmentCollection.whereEqualTo("quizId",quizId)
                .get()
                .addOnSuccessListener {querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // No document found, create a new one
                        val newDocument = hashMapOf(
                            "quizId" to quizId,
                            "attemptList" to mutableListOf(attempt)
                        )

                        // Add the new document to Firestore
                        enrollmentCollection
                            .add(newDocument)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this,"updated Successfully",Toast.LENGTH_SHORT).show()
                                // New document added successfully
                                // Handle success if needed
                            }
                            .addOnFailureListener { e ->
                                // Handle failures
                            }
                    }
                    else{
                        var document = querySnapshot.documents[0]
                        val attemptList = document.get("attemptList") as MutableList<Attempt>?
                        attemptList!!.add(attempt)
                        document.reference.update("attemptList",attemptList)
                            .addOnSuccessListener {
                                Toast.makeText(this,"updated Successfully",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {

                            }
                    }
                }
                .addOnFailureListener {

                }
    }
//    private fun updateStudentAttemptToFirebase(quizId : String, attempt: Attempt){
//        val collectionRef = FirebaseFirestore.getInstance().collection("quizAttempts").document(quizId)
//        collectionRef.update(studentId,attempt)
//            .addOnSuccessListener {
//                Toast.makeText(this,"Result updated successfully",Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//
//            }
////        collectionRef.get().addOnSuccessListener { documentSnapshot ->
////            if (documentSnapshot.exists()) {
////                val data = documentSnapshot.data
////                if (data != null) {
////                    val map = data as? MutableMap<String, Attempt>
////
////                    // Assuming "your_map_field_name" is the name of your map field in Firestore
////                    map?.let {
////                        // Update the value for a specific key in the map
////                        it[studentId]?.apply {
////                            // Update the values as needed
////
////                        }
////
////                        // Update the map field in Firestore
////                        collectionRef.update("your_map_field_name", map)
////                            .addOnSuccessListener {
////                                // Map field updated successfully
////                            }
////                            .addOnFailureListener { exception ->
////                                // Handle the failure to update the map field
////                            }
////                    }
////                }
////            }
////        collectionRef.document(quizId).set(attempt)
////            .addOnSuccessListener {
////                Toast.makeText(this,"Result updated successfully",Toast.LENGTH_SHORT).show()
////            }.addOnFailureListener {
////                showProgressBar(false)
////                Toast.makeText(this,"Some Error Occurred, Please Try Again",Toast.LENGTH_SHORT).show()
////            }
//    }
    private fun setUpEventListener() {
//        timer.start()
        btnPrevious.setOnClickListener {
            index--
            bindViews()
        }

        btnNext.setOnClickListener {
            index++
            bindViews()
        }

        btnSubmit.setOnClickListener {
            if(mode=="teacher"){
                return@setOnClickListener
                Toast.makeText(this,"preview ended",Toast.LENGTH_SHORT).show()
                super.onBackPressed()
                timer.cancel()
                finish()
            }
//            Toast.makeText(this,"ended",Toast.LENGTH_SHORT).show()
            showProgressBar(true)
            updateAttempt()
            calculateScore()
            if(mode!="teacher"){
//                Toast.makeText(this,"mode implemented",Toast.LENGTH_SHORT).show()
                updateStudentAttempt(quizzes!![0].id)
                Log.d("mode imp",mode)
//                addResponses(quizzes!![0])
            }
//            Toast.makeText(this,"mode implemented",Toast.LENGTH_SHORT).show()
            val intent = Intent(this,TeacherHomeActivity::class.java)
            startActivity(intent)
            finishAffinity()
//            Log.d("FINALQUIZ", questions.toString())
//            val intent = Intent(this, ResultActivity::class.java)
//            val json  = Gson().toJson(quizzes!![0])
////            Toast.makeText(this,json.toString(),Toast.LENGTH_SHORT).show()
//            intent.putExtra("QUIZ", json)
//            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
        finish()
    }
    private fun addResponses(quiz : Quiz) {
        val collectionRef = FirebaseFirestore.getInstance().collection("quizzes")
        collectionRef.document(quiz.id).set(quiz)
            .addOnSuccessListener {
                Toast.makeText(this,"Result updated successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                showProgressBar(false)
                Toast.makeText(this,"Some Error Occurred, Please Try Again",Toast.LENGTH_SHORT).show()
            }
    }
    private fun calculateScore() {
        score = 0
        for (entry in quizzes!![0].questions.entries) {
            val question = entry.value
            if (question.answer == question.userAnswer) {
                score += quizzes!![0].marksPerQuestion
            }
        }
//        val txtScore = findViewById<TextView>(R.id.test_score)
//        txtScore.text = "Your Score : $score"
        quizzes!![0].score=score
    }

    private fun updateAttempt() {
        var index = 1
        while(index <= questions!!.size){
            questions!!["$index"]!!.userAnswer= optionSelectorList[index-1].userAnswer
            index=index+1
        }
        quizzes!![0].isAttempted=true
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
        val optionList = findViewById<RecyclerView>(R.id.optionList)
        if(questions!!.size==0){
            Toast.makeText(this,"No questions in the Test",Toast.LENGTH_SHORT).show()
            return
        }
        val question = questions!!["$index"]
        val qno = index
        tvqNo.text= "Q.$qno Single Choice"
        val imageMap = quizzes!![0].imMap
        val optionSelector = optionSelectorList[index-1]
        question?.let {
            loadBase64ImageWithUrl(imageMap[it.imageUrl],it.ytop,it.yend)
//            setImageWithData(it.imageUrl,it.ytop,it.yend);
            val optionAdapter = OptionAdapter(this, optionSelector,it,0)
            optionList.layoutManager = LinearLayoutManager(this)
            optionList.adapter = optionAdapter
            optionList.setHasFixedSize(true)
        }
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
                        optionSelectorList = MutableList(questions!!.size) { OptionSelector(userAnswer = "neg")}
                        bindViews()
                        timer = object : CountDownTimer(quizzes!![0].duration.toLong(), 1000) {

                            // Callback function, fired on regular interval
                            override fun onTick(millisUntilFinished: Long) {
                                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                        TimeUnit.HOURS.toMinutes(hours)
                                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(minutes) -
                                        TimeUnit.HOURS.toSeconds(hours)
                                val timerText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                                tvTimer.text = timerText
                            }

                            // Callback function, fired
                            // when the time is up
                            override fun onFinish() {
                                btnSubmit.callOnClick()
                            }
                        }
                        showProgressBar(false)
                        timer.start()
                    }
                }
        }
    }
    private fun addDummyData() {
        questions = mutableMapOf("question1" to Question("https://www.researchgate.net/publication/255640421/figure/fig1/AS:392587958603776@1470611671200/Sample-image-and-its-feature-extraction-results-Left-Original-image-right-segmented.png",300,317,"option2",""))
//        questions!!.put("question2", questions!!["question1"]!!)
        val question3 = Question("https://drive.google.com/file/d/140erkr0zjU_Y52GUpxeCcqmkwa_Bx7Qt/view?usp=share_link")
        questions!!.put("question2", Question("kkk"))
        questions!!.put("question3",Question("kkdd"))
        quizzes = mutableListOf(Quiz("1","title", questions!!))
        firestore = FirebaseFirestore.getInstance()
        val firebasecollection = firestore.collection("quizzes")
        firebasecollection.document("quiz1").set(quizzes!![0])
//        questions = mutableMapOf("question1" to question3)
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
        val imageView = findViewById<ImageView>(R.id.question_Image)
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
                    val croppedImageView = findViewById<ImageView>(R.id.question_Image)
                    croppedImageView.setImageBitmap(croppedBitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Not implemented
                }
            })
    }


}
package com.team.testscanner.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team.testscanner.R
import com.team.testscanner.adapters.OptionAdapter
import com.team.testscanner.models.OptionSelector
import com.team.testscanner.models.Question
import com.team.testscanner.models.Quiz
import java.net.URL

class TestScreen : AppCompatActivity() {
    var quizzes : MutableList<Quiz>? = null
    var questions: MutableMap<String, Question>? = null
    var index = 1
    lateinit var btnNext : Button
    lateinit var btnPrevious : Button
    lateinit var btnSubmit : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_screen)
         btnPrevious = findViewById<Button>(R.id.btnPrevious)
         btnNext = findViewById<Button>(R.id.btnNext)
         btnSubmit = findViewById<Button>(R.id.btnSubmit)
        bindViews()
    }
    private fun setUpEventListener() {

        btnPrevious.setOnClickListener {
            index--
//            bindViews()
        }

        btnNext.setOnClickListener {
            index++
//            bindViews()
        }

//        btnSubmit.setOnClickListener {
//            Log.d("FINALQUIZ", questions.toString())
//
//            val intent = Intent(this, ResultActivity::class.java)
//            val json  = Gson().toJson(quizzes!![0])
//            intent.putExtra("QUIZ", json)
//            startActivity(intent)
//        }
    }
    private fun bindViews() {

        addDummyData()
        btnPrevious.visibility = View.GONE
        btnSubmit.visibility = View.GONE
        btnNext.visibility = View.GONE

        if(index == 1){ //first question
            btnNext.visibility = View.VISIBLE
        }
        else if(index == questions!!.size) { // last question
            btnSubmit.visibility = View.VISIBLE
            btnPrevious.visibility = View.VISIBLE
        }
        else{ // Middle
            btnPrevious.visibility = View.VISIBLE
            btnNext.visibility = View.VISIBLE
        }
        val optionList = findViewById<RecyclerView>(R.id.optionList)
        val question = questions!!["question$index"]
        question?.let {
            var optionSelector = OptionSelector()
//            setImageWithData(it.imageUrl,it.ytop,it.yend);
            val optionAdapter = OptionAdapter(this, optionSelector)
            optionList.layoutManager = LinearLayoutManager(this)
            optionList.adapter = optionAdapter
            optionList.setHasFixedSize(true)
        }
    }

    private fun addDummyData() {
        questions = mutableMapOf("question1" to Question("https://drive.google.com/file/d/140erkr0zjU_Y52GUpxeCcqmkwa_Bx7Qt/view?usp=share_link",300,317,"",""))
    }

    private fun setImageWithData(imageUrl: String, ytop: Int, yend: Int) {
        val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
        val croppedBitmap = Bitmap.createBitmap(bitmap, 0, ytop, bitmap.width, yend)
        val croppedImageView = findViewById<ImageView>(R.id.question_Image)
        croppedImageView.setImageBitmap(croppedBitmap)
    }


}
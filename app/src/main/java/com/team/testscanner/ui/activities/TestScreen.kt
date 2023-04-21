package com.team.testscanner.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
        setUpEventListener()
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
        //yet to implement firebase database and delete this dummydatafunction
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
            setImageWithData(it.imageUrl,it.ytop,it.yend);
            val optionAdapter = OptionAdapter(this, optionSelector)
            optionList.layoutManager = LinearLayoutManager(this)
            optionList.adapter = optionAdapter
            optionList.setHasFixedSize(true)
        }
    }

    private fun addDummyData() {
        questions = mutableMapOf("question1" to Question("https://www.researchgate.net/publication/255640421/figure/fig1/AS:392587958603776@1470611671200/Sample-image-and-its-feature-extraction-results-Left-Original-image-right-segmented.png",300,317,"",""))
        questions!!.put("question2", questions!!["question1"]!!)
        val question3 =Question("https://drive.google.com/file/d/140erkr0zjU_Y52GUpxeCcqmkwa_Bx7Qt/view?usp=share_link",300,317,"","")
        questions!!.put("question3",question3)
        questions = mutableMapOf("question1" to question3)
    }

    private fun setImageWithData(imageUrl: String, ytop: Int, yend: Int) {
//        val imageView = findViewById<ImageView>(R.id.question_Image)
//        Glide.with(this)
//            .load(imageUrl)
//            .into(imageView)
//
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
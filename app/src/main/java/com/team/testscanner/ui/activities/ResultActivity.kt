package com.team.testscanner.ui.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import com.google.gson.Gson
import com.team.testscanner.R
import com.team.testscanner.models.Quiz
import org.w3c.dom.Text

class ResultActivity : AppCompatActivity() {
    lateinit var quiz : Quiz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        setUpViews()
//        setAnswerView()   //// used for showing result with the help of html but not using this
    }
    private fun setUpViews() {
        val quizData = intent.getStringExtra("QUIZ")
        quiz = Gson().fromJson<Quiz>(quizData, Quiz::class.java)
        calculateScore()
//        setAnswerView()
    }
//    private fun setAnswerView() {
//        val builder = StringBuilder("")
//        for (entry in quiz.questions.entries) {
//            val question = entry.value
//            builder.append("<font color'#18206F'><b>Question: ${question.imageUrl}</b></font><br/><br/>")
//            builder.append("<font color='#009688'>Answer: ${question.answer}</font><br/><br/>")
//        }
//        val txtAnswer = findViewById<TextView>(R.id.txtAnswer)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            txtAnswer.text = Html.fromHtml(builder.toString(), Html.FROM_HTML_MODE_COMPACT);
//        } else {
//            txtAnswer.text = Html.fromHtml(builder.toString());
//        }
//    }

    private fun calculateScore() {
        var score = 0
        for (entry in quiz.questions.entries) {
            val question = entry.value
            if (question.answer == question.userAnswer) {
                score += 10
            }
        }
        val txtScore = findViewById<TextView>(R.id.txtScore)
        txtScore.text = "Your Score : $score"
    }
}
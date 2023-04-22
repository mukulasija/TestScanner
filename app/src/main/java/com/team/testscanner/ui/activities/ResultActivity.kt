package com.team.testscanner.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.gson.Gson
import com.team.testscanner.R
import com.team.testscanner.models.Quiz

class ResultActivity : AppCompatActivity() {
    lateinit var quiz : Quiz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        setUpViews()
    }
    private fun setUpViews() {
        val quizData = intent.getStringExtra("QUIZ")
        quiz = Gson().fromJson<Quiz>(quizData, Quiz::class.java)
        calculateScore()
//        setAnswerView()
    }
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
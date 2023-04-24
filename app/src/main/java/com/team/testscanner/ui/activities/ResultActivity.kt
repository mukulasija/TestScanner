package com.team.testscanner.ui.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
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
        addScoreToFirebase()
        val analysisBtn = findViewById<Button>(R.id.viewAnalysis)
        analysisBtn.setOnClickListener {

        }
//        setAnswerView()
    }

    private fun addScoreToFirebase() {
        val collectionRef = FirebaseFirestore.getInstance().collection("quizzes")
        collectionRef.document(quiz.id).set(quiz)
            .addOnSuccessListener {
                Toast.makeText(this,"Result updated successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this,"Some Error Occurred",Toast.LENGTH_SHORT).show()
            }
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
                score += quiz.marksPerQuestion
            }
        }
        val txtScore = findViewById<TextView>(R.id.txtScore)
        txtScore.text = "Your Score : $score"
        quiz.score=score
    }
}
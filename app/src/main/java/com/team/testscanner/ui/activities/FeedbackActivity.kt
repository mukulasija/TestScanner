package com.team.testscanner.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R

class FeedbackActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        val editTextFeedback = findViewById<EditText>(R.id.editTextFeedback)
        val buttonSubmitFeedback = findViewById<Button>(R.id.buttonSubmitFeedback)

        buttonSubmitFeedback.setOnClickListener {
            val feedback = editTextFeedback.text.toString().trim()
            if (feedback.isNotEmpty()) {
                // Create a HashMap to store feedback data
                val feedbackData = HashMap<String, Any>()
                feedbackData["feedbackText"] = feedback

                // Add the feedback data to the Firestore "feedbacks" collection
                db.collection("feedbacks")
                    .add(feedbackData)
                    .addOnSuccessListener { documentReference ->
                        showToast("Feedback submitted successfully")
                        finish()
                    }
                    .addOnFailureListener { e ->
                        showToast("Error submitting feedback: ${e.message}")
                    }
            } else {
                showToast("Please enter your feedback")
            }
        }
    }
    private fun showToast(message: String) {
        // Replace with your toast implementation or feedback handling
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
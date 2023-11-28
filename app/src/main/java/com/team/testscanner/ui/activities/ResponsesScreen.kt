package com.team.testscanner.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.team.testscanner.R
import com.team.testscanner.adapters.ClassroomAdapter
import com.team.testscanner.adapters.ResponsesAdapter
import com.team.testscanner.databinding.ActivityClassroomBinding
import com.team.testscanner.databinding.ActivityResponsesScreenBinding
import com.team.testscanner.models.Attempt
import com.team.testscanner.models.Quiz

/// TODO when it comes back to this activity recycler view have to be fetched again to fetch latest scores
class ResponsesScreen : AppCompatActivity() {
    private lateinit var binding: ActivityResponsesScreenBinding
    private var quizId: String = ""
    private var attemptList: MutableList<Attempt> = mutableListOf()
    private lateinit var responsesAdapter: ResponsesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResponsesScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        quizId = intent.getStringExtra("quizId").toString()
        fbase()
        binding.btnAddKey.setOnClickListener {
            val intent = Intent(this,AnalysisActivity::class.java)
            intent.putExtra("id",quizId)
            intent.putExtra("mode","update")
            startActivity(intent)
        }
    }

    private fun fbase(){
        val db = FirebaseFirestore.getInstance()

// Replace "documentId" with the ID of the document you want to retrieve
        val documentId = quizId
        attemptList.clear()
// Reference to the document in the collection
        val docRef = db.collection("quizAttempts").whereEqualTo("quizId",quizId)
            .get()
            .addOnSuccessListener {documents ->
                if (documents.isEmpty) {
                    // Handle case where no matching document is found
                } else {
                    // If multiple documents match, loop through them
                    for (document in documents) {
                        Toast.makeText(this,"document exist",Toast.LENGTH_SHORT).show()
                        val quizId = document.getString("quizId")
                        val attemptMap = document.get("attemptList") as List<MutableMap<String,Any>>
                        for(item in attemptMap){
                            val selectedOptions = item.getValue("selectedOptions") as List<String>
                            val score = item.getValue("score").toString()
                            val studentId = item.getValue("studentId").toString()
                            val studentName = item.getValue("studentName").toString()
                            val newAttempt : Attempt = Attempt(score=score.toInt(), studentId = studentId, studentName = studentName, selectedOptions = selectedOptions)
                            attemptList.add(newAttempt)
                        }
                        displayDataInRecyclerView(attemptList)
//                        val scoreList = attemptMap[0].getValue("selectedOptions") as List<String>
//                        val item = attemptMap[0]
//                        val scoreList = item.getValue("selectedOptions") as List<String>
//                        val score = item.getValue("score").toString()
//                        val studentId = item.getValue("studentId").toString()
//                        val studentName = item.getValue("studentName").toString()
//                        Toast.makeText(this,studentId.toString(),Toast.LENGTH_SHORT).show()
//                        val attemptMap : Array<Map<String,Any>> = document.get("attemptList") as Array<Map<String, Any>>
//                        for(attempt in attemptMap){

//                        }
//                        Toast.makeText(this,score.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }

// Retrieve the document
//        docRef.get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    // Access specific fields from the document
//                    val quizId = documentSnapshot.getString("quizId")
//                    val attemptList = documentSnapshot.getString("attemptList")
//                    Toast.makeText(this,attemptList.toString(),Toast.LENGTH_SHORT).show()
//                } else {
//                    // Document doesn't exist
//                    println("Document does not exist")
//                }
//            }
//            .addOnFailureListener { exception ->
//                // Handle any errors while fetching data
//                println("Error getting document: $exception")
//            }
    }
    private fun displayDataInRecyclerView(dataList: MutableList<Attempt>) {
        // Initialize RecyclerView and set the adapter
        val recyclerView: RecyclerView = binding.rvQuizResponses
        val radapter = ResponsesAdapter(this,quizId,dataList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = radapter
    }
}



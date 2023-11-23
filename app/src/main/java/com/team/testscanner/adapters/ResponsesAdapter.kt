package com.team.testscanner.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.team.testscanner.R
import com.team.testscanner.models.Attempt
import com.team.testscanner.models.Quiz
import com.team.testscanner.ui.activities.AnalysisActivity

class ResponsesAdapter(val context : Context, private val quizId : String,private val attemptList : List<Attempt>):RecyclerView.Adapter<ResponsesAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textTitle: TextView =itemView.findViewById(R.id.tv_studentName)
        val textScore: TextView =itemView.findViewById(R.id.tv_studentScore)
        val resultsButton : Button = itemView.findViewById(R.id.btn_analysis)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_student_response,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentAttempt=attemptList.get(position)
        val score=currentAttempt.score
//        var red = false
        holder.textScore.text= "Score : $score"
//        holder.textScore.setTextColor(Color.BLACK)
        holder.textTitle.text=currentAttempt.studentName
        holder.resultsButton.text="Analysis"
//        if(!currentItem.isKeyAvailable){
//            holder.textScore.setTextColor(Color.RED)
//            holder.textScore.text="Please Add answer key"
//            holder.resultsButton.text="Add Key"
//            red=true
//        }
        holder.resultsButton.setOnClickListener {
            val intent = Intent(context,AnalysisActivity::class.java)
            intent.putExtra("id",quizId)
            intent.putExtra("attemptObject", currentAttempt)
            intent.putExtra("mode","get")
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return attemptList.size
    }
}
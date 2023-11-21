package com.team.testscanner.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.team.testscanner.R
import com.team.testscanner.models.Quiz
import com.team.testscanner.ui.activities.ResponsesScreen
import com.team.testscanner.ui.activities.TestScreen
import java.util.concurrent.TimeUnit

class MyAdapter(val context : Context, private val quiz:MutableList<Quiz>,val mode : String):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.temp_layout,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=quiz[position]
        holder.textTitle.text=currentItem.title
        holder.textDesc.text= onTick(currentItem.duration)
        holder.startBtn.setOnClickListener {
            Toast.makeText(context,quiz[position].title,Toast.LENGTH_SHORT).show()
            val intent = Intent(context, TestScreen::class.java)
            intent.putExtra("id", quiz[position].id)
            intent.putExtra("mode",mode)
            context.startActivity(intent)
        }
        if(mode=="teacher"){
            holder.startBtn.text = "See Test Preview"
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ResponsesScreen::class.java)
                intent.putExtra("id", quiz[position].id)
                intent.putExtra("mode",mode)
                context.startActivity(intent)
            }
//            holder.startBtn.isClickable = false
        }
    }
    private fun onTick(millisUntilFinished: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                TimeUnit.HOURS.toMinutes(hours)
        return String.format("%02dH:%02dM", hours, minutes)
    }

    override fun getItemCount(): Int {
        return quiz.size
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textTitle:TextView=itemView.findViewById(R.id.test_title_text_preview)
        val textDesc:TextView=itemView.findViewById(R.id.test_score_temp_preview)
        val startBtn : Button = itemView.findViewById(R.id.check_box_preview)
    }


}
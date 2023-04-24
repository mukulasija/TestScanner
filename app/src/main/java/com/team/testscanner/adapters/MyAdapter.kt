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
import com.team.testscanner.ui.activities.TestScreen

class MyAdapter(val context : Context, private val quiz:MutableList<Quiz>):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.temp_layout,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=quiz[position]
        holder.textTitle.text=currentItem.title
        holder.textDesc.text=currentItem.id
        holder.startBtn.setOnClickListener {
            Toast.makeText(context,quiz[position].title,Toast.LENGTH_SHORT).show()
            val intent = Intent(context, TestScreen::class.java)
            intent.putExtra("id", quiz[position].id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return quiz.size
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textTitle:TextView=itemView.findViewById(R.id.test_title_text)
        val textDesc:TextView=itemView.findViewById(R.id.test_score)
        val startBtn : Button = itemView.findViewById(R.id.start_button)
    }


}
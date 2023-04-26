package com.team.testscanner.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.team.testscanner.R
import com.team.testscanner.models.HighStart
import com.team.testscanner.ui.activities.TestScreen


class CheckBoxAdapter(val context : Context, private val hstartList:MutableList<HighStart>): RecyclerView.Adapter<CheckBoxAdapter.CheckViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.layout_prev_select,parent,false)
        return CheckViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CheckViewHolder, position: Int) {
        val currentItem=hstartList[position]
        holder.textTitle.text=currentItem.description
        holder.textDesc.text= ""
        holder.startCkbox.isChecked = currentItem.tick
        holder.startCkbox.setOnClickListener {
            currentItem.tick= holder.startCkbox.isChecked
            Toast.makeText(context,"this",Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return hstartList.size
    }

    inner class CheckViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textTitle: TextView =itemView.findViewById(R.id.test_title_text_preview)
        val textDesc: TextView =itemView.findViewById(R.id.test_score_temp_preview)
        val startCkbox : CheckBox = itemView.findViewById(R.id.check_box_preview)
    }


}
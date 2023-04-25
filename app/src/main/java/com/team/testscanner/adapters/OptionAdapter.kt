package com.team.testscanner.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team.testscanner.R
import com.team.testscanner.models.OptionSelector
import com.team.testscanner.models.Question

class OptionAdapter(val context: Context, val op: OptionSelector, val question: Question,val mode :Int) :
    RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    private var options: List<String> =
        listOf(op.option1, op.option2, op.option3, op.option4)

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var optionView = itemView.findViewById<TextView>(R.id.quiz_option)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.option_item, parent, false)
        return OptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        if(mode==1){
            holder.optionView.text = options[position]
            holder.itemView.setBackgroundResource(R.drawable.option_item_bg)
            if(options[position]==question.userAnswer){
                holder.itemView.setBackgroundResource(R.drawable.option_item_selected_bg)
            }
            else{
                holder.optionView.text=options[position]
//                holder.itemView.setBackgroundResource(R.drawable.option_item_bg)
            }
            if(options[position]==question.answer){
                    holder.itemView.setBackgroundResource(R.drawable.option_item_green_bg)
            }
            else{
                holder.optionView.text=options[position]
//                holder.itemView.setBackgroundResource(R.drawable.option_item_bg)
            }
        }
        else{
            holder.optionView.text = options[position]
            holder.itemView.setOnClickListener {
                op.userAnswer = options[position]
                notifyDataSetChanged()
            }
            if (op.userAnswer == options[position]) {
                holder.itemView.setBackgroundResource(R.drawable.option_item_selected_bg)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.option_item_bg)
            }
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }
}

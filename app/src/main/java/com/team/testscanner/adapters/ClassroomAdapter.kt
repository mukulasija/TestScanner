package com.team.testscanner.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.utils.widget.ImageFilterButton
import androidx.recyclerview.widget.RecyclerView
import com.team.testscanner.R
import com.team.testscanner.models.Classroom
import com.team.testscanner.ui.activities.ClassroomActivity

class ClassroomAdapter(val context : Context, private val classroomList:MutableList<Classroom>): RecyclerView.Adapter<ClassroomAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_class_card,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=classroomList[position]
        holder.textClassroomName.text=currentItem.classroomName
//        holder.btnthreeDots.setOnClickListener {
//            TODO("three dots button menu yet to implement")
//            Toast.makeText(context,classroomList[position].title + "three dots clicked", Toast.LENGTH_SHORT).show()
//        }
        holder.itemView.setOnClickListener {
            Toast.makeText(context,classroomList[position].classroomId, Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ClassroomActivity::class.java)
            intent.putExtra("classroomId", classroomList[position].classroomId)
            context.startActivity(intent)
        }
        holder.textTeacherName.text= currentItem.classroomTeacherName
    }

    override fun getItemCount(): Int {
        return classroomList.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textClassroomName: TextView =itemView.findViewById(R.id.tvClassroomName)
        val textTeacherName: TextView =itemView.findViewById(R.id.tvTeacherName)
        val btnthreeDots : ImageFilterButton= itemView.findViewById(R.id.btnClassroomMenu)
    }


}
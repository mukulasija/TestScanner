package com.team.testscanner.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team.testscanner.R

class MyAdapter(private val testDataList:ArrayList<TestData>):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.temp_layout,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=testDataList[position]
        holder.textTitle.text=currentItem.testtitle
        holder.textDesc.text=currentItem.testdescription
    }

    override fun getItemCount(): Int {
        return testDataList.size
    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textTitle:TextView=itemView.findViewById(R.id.test_title_text)
        val textDesc:TextView=itemView.findViewById(R.id.test_title_desc)
    }


}
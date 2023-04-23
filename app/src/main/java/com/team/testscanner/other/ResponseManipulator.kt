package com.team.testscanner.other

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.team.testscanner.models.Question
import org.json.JSONObject
import kotlin.math.abs

class ResponseManipulator(private val context: Context,private var response: JSONObject) {
    fun getgetquestionlist(): MutableList<Question> {
        return mutableListOf(Question())
    }
    val responses = response
    fun getTextAnnotations(){
        val texts = response.getJSONArray("responses").getJSONObject(0).getJSONArray("textAnnotations")
        for(i in 0 until texts.length()){
            val text = texts.getJSONArray(i)
        }
//        val texts = response.getJSONArray("responses").getJSONArray(0)
        print(texts)
    }
    data class MyObject(
        val description: String,
        val boundingPoly: BoundingPoly
    )

    data class BoundingPoly(
        val vertices: List<Vertex>
    )

    data class Vertex(
        val x: Int,
        val y: Int
    )

    data class Previous(var ytop: Int, var ydown: Int)
    data class Start(var description:String,var ytop: Int, var ydown: Int)
    fun main(){

        var prev = Previous( -100000, -100000 )
        var start=mutableListOf<Start>()
        val gson  = Gson()
        var new_ytop:Int=0;
        var new_ydown:Int=0;
        val texts = response.getJSONArray("responses").getJSONObject(0).getJSONArray("textAnnotations")
        for(i in 0 until texts.length()){
            val textJson = texts.getJSONArray(i)
            val text = gson.fromJson(textJson.toString(),MyObject::class.java)
            new_ytop = text.boundingPoly.vertices[0].y
            new_ydown = text.boundingPoly.vertices[2].y
            if (!(abs(prev.ytop - new_ytop)<= 10 && abs(prev.ydown - new_ydown)<= 10 )){
                start.add(Start(text.description,new_ytop,new_ydown))
                prev.ytop = new_ytop
                prev.ydown = new_ydown
            }
        }

        for (value in start)
            println(value)

        start.sortBy{it.ytop}


        var questions_start = mutableListOf<Start>()
        var questions_end = mutableListOf<Start>()

        var index:Int=0;

        for(value in start){
//            if(value.description[0].isDigit()==true){
            if(value.description[0]=='Q'){
                if(questions_start.size>0){
                    questions_end.add(start[index-1])
                }
                questions_start.add(value)
            }
            index=index+1
        }
        questions_end.add(start[index-1])

        for (value in questions_start)
        {
            Log.d("manipulator",value.toString())
            print(value)
        }

        for (value in questions_end )
            print(value)

    }
}


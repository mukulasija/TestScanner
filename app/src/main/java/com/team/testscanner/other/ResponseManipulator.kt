package com.team.testscanner.other

import android.content.Context
import com.team.testscanner.models.Question
import org.json.JSONObject

class ResponseManipulator(private val context: Context,private var response: JSONObject) {
    fun getResponse() : JSONObject{
//        print(response)
        return response
    }
    fun getFirstLetter(){
        val texts = response.getJSONArray("textAnnotations")
        val start = mutableListOf<JSONObject>()
        var prev = JSONObject()
        prev.put("ytop", -100000)
        prev.put("ydown", -100000)

        for (i in 1 until texts.length()) {
            val text = texts.getJSONObject(i)
            println(text.getString("description"))
//            val new_ytop = text.getJSONObject("boundingPoly").getJSONArray("vertices").getJSONObject(0).getInt("y")
//            val new_ydown = text.getJSONObject("boundingPoly").getJSONArray("vertices").getJSONObject(2).getInt("y")
//
//            if (abs(prev.getInt("ytop") - new_ytop) <= 10 && abs(prev.getInt("ydown") - new_ydown) <= 10) {
//                // Do nothing
//            } else {
//                val value = JSONObject()
//                value.put("description", text.getString("description"))
//                value.put("ytop", new_ytop)
//                value.put("ydown", new_ydown)
//                start.add(value)
//
//                prev.put("ytop", new_ytop)
//                prev.put("ydown", new_ydown)
//            }
        }

//        for (value in start) {
//            println(value)
//        }
//
//        println("-----------------------")
//
//        val questions_start = mutableListOf<JSONObject>()
//        val questions_end = mutableListOf<JSONObject>()
//
//        var prevIndex = 0
//        var index = 0
//
//        for (value in start) {
//            if (value.getString("description").isNumeric()) {
//                if (questions_start.size > 0) {
//                    questions_end.add(start[index-1])
//                }
//                questions_start.add(value)
//            }
//            index += 1
//        }
//
//        questions_end.add(start[index-1])
//
//        for (value in questions_start) {
//            println(value)
//        }
//
//        println("-----------------------")
//
//        for (value in questions_end) {
//            println(value)
//        }
//    }

}

    fun getgetquestionlist(): MutableList<Question> {
        return mutableListOf(Question())
    }
}
//
//fun String.isNumeric(): Boolean {
//    return this.matches("-?\\d+(\\.\\d+)?".toRegex())
//}

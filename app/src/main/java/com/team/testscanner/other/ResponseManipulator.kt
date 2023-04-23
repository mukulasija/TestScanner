package com.team.testscanner.other

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.team.testscanner.models.Question
import com.team.testscanner.models.Quiz
import org.json.JSONObject
import kotlin.math.abs

class ResponseManipulator(private val context: Context,private var response: JSONObject,imageUri: Uri) {
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
    fun main() : MutableList<Question>{

        var prev = Previous( -100000, -100000 )
        var start=mutableListOf<Start>()
        val gson  = Gson()
        var new_ytop:Int=0;
        var new_ydown:Int=0;
        val texts = response.getJSONArray("responses").getJSONObject(0).getJSONArray("textAnnotations")
        for(i in 1 until texts.length()){
            val textJson = texts.getJSONObject(i)
            val text = gson.fromJson(textJson.toString(),MyObject::class.java)
            new_ytop = text.boundingPoly.vertices[0].y
            new_ydown = text.boundingPoly.vertices[2].y
            if (!(abs(prev.ytop - new_ytop)<= 10 && abs(prev.ydown - new_ydown)<= 10 )){
                start.add(Start(text.description,new_ytop,new_ydown))
                prev.ytop = new_ytop
                prev.ydown = new_ydown
            }
        }

//        for (value in start)
//            println(value)

        start.sortBy{it.ytop}


        var questions_start = mutableListOf<Start>()
        var questions_end = mutableListOf<Start>()

        var index:Int=0;

//        format for 1,2,3 ......
        for(value in start){
//            if(value.description[0].isDigit()==true && value.description.length==1){
//                if(value.description[0].isDigit() && value.description.length==2 && value.description[1]=='.' ){
            if(value.description[0]=='Q'){
                if(questions_start.size>0){
                    questions_end.add(start[index-1])
                }
                questions_start.add(value)
            }
            index=index+1
        }

//        for(value in start){
//            if(value.description[0].isDigit() && value.description[1]=='.' && value.description.length==2){
//
//            }
//        }
        questions_end.add(start[index-1])
        print("--------------")
        for (value in questions_start)
        {
            Log.d("manipulator",value.toString())
            print(value)
        }
        print("--------------")


        for (value in questions_end )
        {
            Log.d("manipulator",value.toString())
            print(value)
        }

        val imageUrl : String = ""

        val questionList : MutableList<Question> = getQuestionList(questions_start,questions_end,imageUrl)
        return questionList
    }

    private fun getQuestionList(
        questionsStart: MutableList<Start>,
        questionsEnd: MutableList<Start>,imageUrl : String
    ): MutableList<Question> {
        val index = 0
        val questionList = mutableListOf<Question>()
        if(questionsStart.size==0)
            return questionList
        while (index<questionsStart.size){
            questionList.add(Question(imageUrl = imageUrl, ytop = questionsStart[index].ytop, yend = questionsEnd[index].ydown))
        }
        return questionList
    }

    fun getImageUrl(imageUri : Uri) : String{
        return ""
    }
//    fun upload(imageUri : Uri){
//        val storageRef = FirebaseStorage.getInstance().reference.child("images")
//        if (imageUri != null) {
//            val imageName = imageUri.lastPathSegment
//            val imageRef = storageRef.child(imageName)
//
//            // Upload the image file to Firebase Storage
//            imageRef.putFile(imageUri).addOnSuccessListener {
//                // Get the download URL of the image file
//                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
//                    // Store the download URL in Firestore
//                    val image = hashMapOf(
//                        "name" to imageName,
//                        "url" to downloadUrl.toString()
//                    )
//                    val db = FirebaseFirestore.getInstance()
//                    db.collection("images").document(imageName).set(image)
//                }
//            }.addOnFailureListener { e ->
//                // Handle the upload error
//                Log.e(TAG, "Upload failed: ", e)
//            }
//        }
//    }
}


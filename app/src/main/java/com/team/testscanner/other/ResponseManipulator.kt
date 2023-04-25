package com.team.testscanner.other

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.team.testscanner.models.Question
import org.json.JSONObject
import kotlin.math.abs

class ResponseManipulator(private val context: Context, private var response: JSONObject,
                          val base64ImageString: String
) {
//    fun getgetquestionlist(): MutableList<Question> {
//        return mutableListOf(Question())
//    }
    var questions_start = mutableListOf<Start>()
    var questions_end = mutableListOf<Start>()
    var imageUrl : String = ""
    var task_done =0
    val questionlist = mutableListOf<Question>()
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
    fun main(questionType: String): MutableList<Question>{
//        upload()
        val prev = Previous( -100000, -100000 )
        val start=mutableListOf<Start>()
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




        var index:Int=0;

//        format for 1,2,3 ......
        for(value in start){
//            if(value.description[0].isDigit()==true && value.description.length==1){
//                if(value.description[0].isDigit() && value.description.length==2 && value.description[1]=='.' ){
//            if(value.description[0]=='Q'){
            if(checkCondition(questionType,value)==true){
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
            Log.d("manipulatorStart",value.toString())
            print(value)
        }
        print("--------------")


        for (value in questions_end )
        {
            Log.d("manipulatorEnd",value.toString())
            print(value)
        }
//        upload()
        getQuestionList()
        return questionlist
//        val questionList : MutableList<Question> = getQuestionList(questions_start,questions_end)
//        return questionList
    }

    private fun checkCondition(questionType: String, value: Start,): Any {
        if(questionType=="1"){
            if(value.description[0].isDigit()==false){
                return false
            }
            for(ch in value.description){
                if(ch.isLetter()){
                    return false
                }
            }
            if(value.description[0].isDigit()==true && value.description.length==1){
                return true
            }
//            for(ch in value.description){
//                if(ch.isLetter()){
//                    return false
//                }
//            }
//            if(value.description[0].isDigit()==true){
//                return true
//            }
//            if(value.description[0].isDigit()==false){
//                return false
//            }
//            if(value.description.length==1){
//                return true
//            }
            var fl=0
            for(ch in value.description){
                if(fl==1 && ch.isDigit()){
                    return false
                }
                if(ch=='.'){
                    fl=1
                }
            }
            if(value.description.length==2){
                if(value.description[1].isDigit()){
                    return true
                }
            }
            if(value.description[0].isDigit() && value.description.length==2 && value.description[1]=='.' ){return true}
            if((value.description.length==3 && value.description[1].isDigit() && value.description[2]=='.')){return true}
        }
        if(questionType=="1."){
            if(value.description[0].isLetter()){
                return false
            }
            if(value.description[0].isDigit()==false){
                return false
            }
            if(value.description.length==1){
                return true
            }
            if(value.description[0].isDigit() && value.description.length==2 && value.description[1]=='.' ){return true}
            for(ch in value.description){
                if(ch.isLetter()){
                    return false
                }
            }
            if(value.description[0].isDigit() && value.description.length==2 && value.description[1]=='.' ){return true}
            if(value.description[0].isDigit() && value.description.length==3 && value.description[1].isDigit() && value.description[2]=='.'){return true}
        }
        if(questionType=="Q"){
            if(value.description[0]=='Q'){
                return true
            }
        }
        if(questionType=="Question"){
            if(value.description[0]=='Q' && value.description.length>1 && (value.description[1]=='U' || value.description[1]=='u')){
                return true
            }
        }
        if(questionType=="question"){
            if(value.description[0]=='q'){
                return true
            }
        }
        return false
    }

    private fun getQuestionList() : MutableList<Question>{
        var index = 0
        if(questions_start.size==0){
            return questionlist
        }
        while (index<questions_start.size){
            questionlist.add(Question(imageUrl = base64ImageString, ytop = questions_start[index].ytop, yend = questions_end[index].ydown))
            index=index+1
        }
        task_done = 1
        return questionlist
    }
//    private fun getQuestionList(
//        questionsStart: MutableList<Start>,
//        questionsEnd: MutableList<Start>
//    ): MutableList<Question> {
//        var index = 0
//        val questionList = mutableListOf<Question>()
//        if(questionsStart.size==0)
//            return questionList
//        while (index<questionsStart.size){
//            questionList.add(Question(imageUrl = imageUrl, ytop = questionsStart[index].ytop, yend = questionsEnd[index].ydown))
//            index=index+1
//        }
//        return questionList
//    }

//    fun upload(){
//        val storageRef = FirebaseStorage.getInstance().reference.child("images")
//        if (imageUri != null) {
//            val imageName = imageUri.lastPathSegment
//            val imageRef = storageRef.child(imageName!!)
//            // Upload the image file to Firebase Storage
//            imageRef.putFile(imageUri).addOnSuccessListener {
//                // Get the download URL of the image file
//                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
//                    Log.d("upload","success")
//                    // Store the download URL in Firestore\
//                    imageUrl = downloadUrl.toString()
//                    task_done=1
////                    getQuestionList()
//                    Toast.makeText(context,"done",Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener { e ->
//                task_done=-1
//                // Handle the upload error
//                Log.e(TAG, "Upload failed: ", e)
//                Toast.makeText(context,"failed",Toast.LENGTH_SHORT).show()
//
//            }
//        }
//    }
}


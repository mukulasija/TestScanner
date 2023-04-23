package com.team.testscanner.models

data class Quiz (
    var id : String = "",
    var title: String = "",
    var isAttempted : Boolean = false,
    var questions: MutableMap<String, Question> = mutableMapOf()
){
    constructor(id : String,title: String,questions: MutableMap<String, Question>) : this(id,title,false, questions =questions )
}

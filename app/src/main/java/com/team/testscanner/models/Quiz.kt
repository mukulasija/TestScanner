package com.team.testscanner.models

import java.time.Duration

data class Quiz (
    var id : String = "",
    var title: String = "",
    var isAttempted : Boolean = false,
    var questions: MutableMap<String, Question> = mutableMapOf(),
    var marksPerQuestion : Int =1,
    var score : Int =0
){
    constructor(id : String,title: String,questions: MutableMap<String, Question>) : this(id,title,false, questions =questions )
}

package com.team.testscanner.models

data class Attempt(
    var studentId : String,
    var studentName : String,
    var selectedOptions : MutableList<String>,
    var score : Int
)
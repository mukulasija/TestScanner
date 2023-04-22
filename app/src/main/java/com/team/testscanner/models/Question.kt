package com.team.testscanner.models

data class Question(
    var imageUrl: String = "",
    var ytop : Int,
    var yend : Int,
    var answer: String = "",
    var userAnswer: String = ""
)
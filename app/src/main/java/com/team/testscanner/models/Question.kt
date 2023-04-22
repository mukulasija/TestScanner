package com.team.testscanner.models

data class Question(
    var imageUrl: String = "",
    var ytop : Int,
    var yend : Int,
    var answer: String = "",
    var userAnswer: String = ""
) {
    constructor(imageUrl: String) : this(imageUrl,10,20,"option1","")
}

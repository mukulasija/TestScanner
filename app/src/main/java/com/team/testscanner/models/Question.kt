package com.team.testscanner.models

data class  Question(
    var imageUrl: String = "",
    var ytop : Int,
    var yend : Int,
    var answer: String = "",
    var userAnswer: String = "",
    var serverAnswer : String = ""
) {
    constructor(imageUrl: String) : this(imageUrl,10,20,"option1","")
    constructor() : this("sample url",10,20,"option1","")
    constructor(imageUrl: String,ytop: Int,yend: Int) :this(imageUrl,ytop, yend,"","")
}

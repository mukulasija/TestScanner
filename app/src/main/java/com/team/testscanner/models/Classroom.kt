package com.team.testscanner.models

data class Classroom(
    var classroomId : String = "",
    var classroomName: String = "",
    var classroomSection : String = "",
    var classroomTeacherName : String = "",
    var classroomTeacherEmail : String = "",
    var classroomQuizMap : MutableMap<String,String> = mutableMapOf()
) {
}
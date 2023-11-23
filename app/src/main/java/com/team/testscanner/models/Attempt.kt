package com.team.testscanner.models

import android.os.Parcelable
import java.io.Serializable


data class Attempt(
    var studentId : String,
    var studentName : String,
    var selectedOptions : List<String> = mutableListOf(),
    var score : Int,
):Serializable

data class ListAttempt(
    var quizId : String,
    var attemptList: Array<Attempt>
)
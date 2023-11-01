package com.team.testscanner.utility

import android.widget.EditText

class MyUtility {
    companion object {
        // Utility function to validate if an EditText is not empty
        fun EditText.isEditTextNotEmpty(): Boolean {
            val text = this.text.toString().trim()
            return text.isNotEmpty()
        }
        fun areEditTextsNotEmpty(vararg editText: EditText) : Boolean{
            for(et in editText){
                val text = et.text.toString().trim()
                if(text.isEmpty()){
                    return false
                }
            }
            return true
        }

        // You can add more utility functions here as needed
    }
}

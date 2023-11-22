package com.team.testscanner.utility

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService

class MyUtility {
    companion object {
        // Utility function to validate if an EditText is not empty
        fun EditText.isEditTextNotEmpty(): Boolean {
            val text = this.text.toString().trim()
            return text.isNotEmpty()
        }
        fun copyTextToClipboard(context: Context, text: String) {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val clipData = ClipData.newPlainText("Copied Text", text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(context,"Classroom Code Copied to Clipboard",Toast.LENGTH_SHORT).show()
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

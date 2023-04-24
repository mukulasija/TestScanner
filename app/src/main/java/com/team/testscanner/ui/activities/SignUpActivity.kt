package com.team.testscanner.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.team.testscanner.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth
        val signupButton:Button =  findViewById(R.id.button_sign_up)
        signupButton.setOnClickListener {
            performSignUp()
        }
    }

    private fun performSignUp(){
        val email=findViewById<EditText>(R.id.edit_username_input_signup)
        val password=findViewById<EditText>(R.id.edit_password_input_signup)
        val confirmPassword=findViewById<EditText>(R.id.edit_password_input_confirm_signup)

        if(email.text.isEmpty() || password.text.isEmpty() || confirmPassword.text.isEmpty() ){
            Toast.makeText(this,"Please fill all fields ",Toast.LENGTH_SHORT)
                .show()
            return
        }
        if(confirmPassword.text.toString() != password.text.toString()){
            Toast.makeText(this,"The password confirmation does not match.",Toast.LENGTH_SHORT)
                .show()
            confirmPassword.setText("")
            return
        }

        val inputEmail=email.text.toString()
        val inputPassword=password.text.toString()

        auth.createUserWithEmailAndPassword(inputEmail,inputPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Error Occurred ${it.localizedMessage}",Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
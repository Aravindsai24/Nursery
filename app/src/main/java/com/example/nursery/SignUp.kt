package com.example.nursery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {
    lateinit var etfullname: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var password2: EditText
    lateinit var register: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        etfullname = findViewById(R.id.etFullName)
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPassword)
        password2 = findViewById(R.id.etPassword2)
        register = findViewById(R.id.register)
    }

    fun RegisterDetails(view: View) {

        if ((password.text.toString() != password2.text.toString()) || !isEmailValid(
                email.text.toString().trim { it <= ' ' }) ||(etfullname.text.toString()=="")
        ) {
            Toast.makeText(this, "Enter Valid Details", Toast.LENGTH_LONG).show()
        } else {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email.text.toString().trim { it <= ' ' },
                    password.text.toString().trim { it <= ' ' }).addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT)
                                .show()
                            val user = FirebaseAuth.getInstance().currentUser

                            val profileUpdates = userProfileChangeRequest {
                                displayName =etfullname.text.toString()
                            }
                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(SignUp::class.java.simpleName, "User profile updated.")
                                    }
                                }
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, GoogleSignInActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Registration Unsuccessful" + task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                )
        }

    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
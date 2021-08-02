package com.academy.octave

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaos.view.PinView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class VerifyOTP : AppCompatActivity() {
    private var btnVerifyCode: Button? = null
    private var verificationId: String? = null
    private val textOTP: EditText? = null
    private var mAuth: FirebaseAuth? = null
    private var pinView: PinView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_otp)
        val extras: Bundle? = intent.extras
        verificationId = extras!!.getString("code")
        mAuth = FirebaseAuth.getInstance()
        pinView = findViewById(R.id.pinView)
        btnVerifyCode = findViewById<Button>(R.id.btnVerifyOTP)
        pinView!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d(
                    "TAG",
                    "onTextChanged() called with: s = [$s], start = [$start], before = [$before], count = [$count]"
                )
                if (count == 6) verifyCode(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
        btnVerifyCode!!.setOnClickListener { v: View? -> verifyCode(pinView!!                                                                                                      .text.toString()) }


    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        // inside this method we are checking if
        // the code entered is correct or not.


        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // if the code is correct and the task is successful
                    // we are sending our user to new activity.
                    val i = Intent(this@VerifyOTP, RegisterActivity::class.java)
                    finish()
                    startActivity(i)
                } else {
                    // if the code is not correct then we are
                    // displaying an error message to the user.
                    Toast.makeText(
                        this@VerifyOTP,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // below method is use to verify code from Firebase.
    private fun verifyCode(code: String) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId!!, code)

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential)
    }
}
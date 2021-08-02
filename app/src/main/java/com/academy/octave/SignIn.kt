package com.academy.octave


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class SignIn : AppCompatActivity(), View.OnClickListener {

    private var mAuth: FirebaseAuth? = null
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)
        mAuth = FirebaseAuth.getInstance()
        editTextEmail = findViewById(R.id.editEmail)
        editTextPassword = findViewById(R.id.editPassword)
        progressBar = findViewById(R.id.progressbar)

        //Register Listener
        findViewById<View>(R.id.textRegister).setOnClickListener(this)

        //Login Listener
        findViewById<View>(R.id.btnSignIn).setOnClickListener(this)

        findViewById<View>(R.id.textForgotPassword).setOnClickListener(this)
        findViewById<View>(R.id.textPrivacyPolicy).setOnClickListener(this)
    }

    private fun userLogin() {
        val email = editTextEmail!!.text.toString().trim { it <= ' ' }
        val password = editTextPassword!!.text.toString()
        if (email.isEmpty()) {
            editTextEmail!!.error = "Email is required"
            editTextEmail!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = "Please enter a valid email"
            editTextEmail!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editTextPassword!!.error = "Password is required"
            editTextPassword!!.requestFocus()
            return
        }
        if (password.length < 6) {
            editTextPassword!!.error = "Minimum length of password should be 6"
            editTextPassword!!.requestFocus()
            return
        }
        progressBar!!.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                progressBar!!.visibility = View.GONE
                if (task.isSuccessful) {
                    finish()
                    val intent = Intent(this@SignIn, Home::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, task.exception!!.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.textRegister -> {
                val register = SpannableString(getString(R.string.Register))
                register.setSpan(UnderlineSpan(), 0, register.length, 0)
                val signupText = findViewById<TextView>(R.id.textRegister)
                signupText.text = register
                signupText.setTextColor(Color.BLUE)
                finish()
                startActivity(Intent(this@SignIn, PhoneVerification::class.java))
            }
            R.id.btnSignIn -> userLogin()
            R.id.textForgotPassword -> {
                val resetPassword = SpannableString(getString(R.string.forgot_your_password))
                resetPassword.setSpan(UnderlineSpan(), 0, resetPassword.length, 0)
                val resetPasswordText = findViewById<TextView>(R.id.textForgotPassword)
                resetPasswordText.text = resetPassword
                resetPasswordText.setTextColor(Color.BLUE)
                finish()
                startActivity(Intent(this@SignIn, ResetPassword::class.java))
            }
            else -> throw IllegalStateException("Unexpected value: " + view.id)
        }
    }
}
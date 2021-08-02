package com.academy.octave

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.academy.octave.ResetPassword
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ResetPassword : AppCompatActivity() {
    private lateinit  var firebaseAuth: FirebaseAuth
    private lateinit var email: TextView
    private lateinit var textLogin: TextView
    private lateinit var btnPasswordReset: Button
    private lateinit var emailText: String
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password)
        email = findViewById(R.id.editPassword)
        btnPasswordReset = findViewById(R.id.btnResetPassword)
        progressBar = findViewById(R.id.progressbarResetPassword)
        progressBar.visibility = View.GONE
        textLogin = findViewById(R.id.textLogin)

        textLogin.setOnClickListener {
            val login = SpannableString("Login")
            login.setSpan(UnderlineSpan(), 0, login.length, 0)
            textLogin.text = login
            textLogin.setTextColor(Color.BLUE)
            finish()
            startActivity(Intent(this@ResetPassword, SignIn::class.java))
        }


        firebaseAuth = FirebaseAuth.getInstance()

        btnPasswordReset.setOnClickListener {
            hideKeyboard(this@ResetPassword)
            progressBar.visibility = View.VISIBLE
            emailText = email.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.error = "Please enter a valid email"
                email.requestFocus()
                progressBar.visibility = View.GONE
            } else {
                firebaseAuth.sendPasswordResetEmail(emailText).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ResetPassword,
                            "Password Reset Link Sent Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ResetPassword,
                            Objects.requireNonNull(task.exception)?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
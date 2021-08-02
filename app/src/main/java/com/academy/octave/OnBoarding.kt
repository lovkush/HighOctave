package com.academy.octave

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OnBoarding : AppCompatActivity() {
    private lateinit var signInButton : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.on_boarding)
        signInButton = findViewById(R.id.sign_in)
        signInButton.setOnClickListener {
            val intent = Intent(this@OnBoarding, SignIn::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.register).setOnClickListener{
            val intent = Intent(this@OnBoarding, PhoneVerification::class.java)
            startActivity(intent)
            finish()
        }


    }
}
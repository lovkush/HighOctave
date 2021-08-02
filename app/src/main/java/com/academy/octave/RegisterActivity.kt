package com.academy.octave

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private var viewModel: UserDataViewModel? = null
    var userData: UserData? = null
    private var email: String? = null
    private var pass: String? = null
    var UID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, Registration1(), "SOMETAG").commit()
        viewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)
        viewModel!!.getSelectedItem().observe(this) { item ->
            userData = item
            Log.d("userdata", "onCreate: " + userData!!.name)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, Registration2(), "SOMETAG").commit()
        }

        viewModel!!.getEmailPass().observe(this) { item ->
            email = item.get(0)
            pass = item.get(1)
            Log.d("TAG", "onCreate: user data" + userData!!.name + userData!!.addLine1)
            userData!!.email = email
            register()
        }
    }

    private fun register() {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(email.toString(), pass.toString())
            .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        UID = mAuth.getUid()
                        uploadData()
                        Toast.makeText(
                            this@RegisterActivity, "Authentication Done.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.getException())
                        Toast.makeText(
                            this@RegisterActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }
    }

    private fun uploadData() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        Log.d(ContentValues.TAG, "uploadData: $reference")

        reference.child("User").child(UID.toString()).setValue(userData) { databaseError, databaseReference ->
            if (databaseError != null) {
                println("Error : $databaseError")
                Log.d("error", "onComplete: $databaseError")
            } else {
                val intent = Intent(this@RegisterActivity, Home::class.java)
                finish()
                startActivity(intent)
                Log.d("TAG", "onComplete: data uploaded")
            }
        }
    }
}
package com.academy.octave

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaos.view.PinView
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class PhoneVerification : AppCompatActivity() {
    // variable for FirebaseAuth class
    private var mAuth: FirebaseAuth? = null
    private val phoneAuthProvider: PhoneAuthProvider? = null
    private var edtPhone : EditText? = null
    var status = 0
    private var verifyOTPBtn: Button? = null
    private val generateOTPBtn: Button? = null
    private var verificationId: String? = null
    private val pinView: PinView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.input_contact)

        // below line is for getting instance
        // of our FirebaseAuth.
        mAuth = FirebaseAuth.getInstance()

        // initializing variables for button and Edittext.
        edtPhone = findViewById(R.id.phone_number)
        verifyOTPBtn = findViewById<Button>(R.id.getOTP)
        edtPhone!!.setText("+91")
        Selection.setSelection(edtPhone!!.text, edtPhone!!.text.length)
        edtPhone!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().startsWith("+91")) {
                    edtPhone!!.setText("+91")
                    Selection.setSelection(edtPhone!!.text, edtPhone!!.text.length)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }
        })
        try {
            requestHint()
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
        verifyOTPBtn!!.setOnClickListener { v: View? ->
            sendVerificationCode(
                edtPhone!!.text.toString()
            )
        }
    }

    @Throws(SendIntentException::class)
    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent = Credentials.getClient(this).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender,
            RESOLVE_HINT, null, 0, 0, 0
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                val credential: Credential? = data!!.getParcelableExtra(Credential.EXTRA_KEY)
                // credential.getId(); <-- E.164 format phone number on 10.2.+ devices
                if (credential != null) edtPhone!!.setText(credential.id)
                Log.d("cred", "onActivityResult: " + credential!!.id)
            }
        }
    }

    private fun sendVerificationCode(number: String) {
        // this method is used for getting
        // OTP on user phone number.

        val options: PhoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // callback method is called on Phone auth provider.
    private val  mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // below method is used when
            // OTP is sent from Firebase
            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                status = 1
                Toast.makeText(this@PhoneVerification, "OTP SENT", Toast.LENGTH_LONG).show()
                verificationId = s
                val intent = Intent(this@PhoneVerification, VerifyOTP::class.java)
                intent.putExtra("code", verificationId)
                finish()
                startActivity(intent)
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {}
            override fun onVerificationFailed(e: FirebaseException) {}
        }

    companion object {
        private const val RESOLVE_HINT = 2
    }
}
package com.academy.octave

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class Registration2 : Fragment() {
    var userData = UserData()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.email_registration, container, false)
    }

    private lateinit var textEmail: TextView
    private lateinit var textPass: TextView
    private lateinit var textRePass: TextView
    private lateinit var email: String
    private lateinit var pass: String
    private lateinit var rePass: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textEmail = view.findViewById<TextView>(R.id.textEmail)
        textPass = view.findViewById<TextView>(R.id.input_password)
        textRePass = view.findViewById<TextView>(R.id.input_reEnterPassword)
        val btn = view.findViewById<Button>(R.id.btnRegister)
        val viewModel: UserDataViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        btn.setOnClickListener {
            // Set a new item
            email = textEmail.text.toString()
            pass = textPass.text.toString()
            rePass = textRePass.text.toString()
            if (validate()) {
                val result = arrayOfNulls<String>(2)
                result[0] = email
                result[1] = pass
                viewModel.selectEmailPass(result)
            }
        }
    }

    private fun validate(): Boolean {
        val result = false
        if (email!!.isEmpty()) {
            textEmail.setError("Email is required")
            textEmail.requestFocus()
            return result
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textEmail.setError("Please enter a valid email")
            textEmail.requestFocus()
            return result
        }
        if (pass!!.isEmpty()) {
            textPass.setError("Password is required")
            textPass.requestFocus()
            return result
        }
        if (pass!!.length < 6) {
            textPass.setError("Minimum length of password should be 6")
            textPass.requestFocus()
            return result
        }
        if (rePass != pass) {
            textRePass.setError("Password does not match.")
            textRePass.requestFocus()
            return result
        }
        return true
    }
}
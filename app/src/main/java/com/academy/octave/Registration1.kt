package com.academy.octave

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class Registration1 : Fragment(), AdapterView.OnItemClickListener {
    var userData = UserData()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.get_user_details, container, false)
    }

    private lateinit var textSignIn: TextView
    private lateinit var textUserName: TextView
    private lateinit var textAdd1: TextView
    private lateinit var textAdd2: TextView
    private lateinit var textCity: AutoCompleteTextView
    private lateinit var textState: AutoCompleteTextView
    private lateinit var textCategory: AutoCompleteTextView
    private lateinit var userName: String
    private var city: String? = null
    private var state: String? = null
    private var add1: String? = null
    private var add2: String? = null
    private var category: String? = null
    private var mRequestQueue: RequestQueue? = null
    private lateinit var editImage: FloatingActionButton
    private lateinit var imageView: ImageView
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var reference: DatabaseReference? = null
    private var viewModel: UserDataViewModel? = null
    private var mAuth: FirebaseAuth? = null
    var s = ArrayList<String>()

    private lateinit var cityAdapter: ArrayAdapter<String>
    lateinit var stateAdapter: ArrayAdapter<String>
    lateinit var categoryAdapter: ArrayAdapter<String>

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRequestQueue = Volley.newRequestQueue(context)
        textSignIn = view.findViewById(R.id.textSignIn)
        textUserName = view.findViewById(R.id.textDocName)
        textAdd1 = view.findViewById(R.id.textDescription)
        textAdd2 = view.findViewById(R.id.textAddLine2)
        textCity = view.findViewById(R.id.selectCity)
        textCategory = view.findViewById(R.id.selectCategory)
        textState = view.findViewById(R.id.selectState)
        editImage = view.findViewById(R.id.editProfilImage)
        imageView = view.findViewById(R.id.docProfileimage)
        viewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
        val contact: String = Objects.requireNonNull(mAuth!!.currentUser)?.phoneNumber!!
        userData.contact = contact.substring(3)

        textSignIn.setOnClickListener { v->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, SignIn::class.java)
            requireActivity().finish()
            startActivity(intent)
        }
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        reference = FirebaseDatabase.getInstance().reference
        initUI(view)
        editImage.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(context, UploadPhoto::class.java)
            startActivityForResult(intent, 2)
        })
    }

    fun verifyData(): Boolean {
        val result = false
        category = textCategory.text.toString()
        if (category == "") {
            textCategory.error = "Category is required"
            textCategory.requestFocus()
            return result
        }
        val pattern = Regex("^[A-Za-z ]+$")
        if (!pattern.containsMatchIn(userName) || userName.length < 3) {
            textUserName.error = "Enter a valid Name"
            textUserName.requestFocus()
            return result
        }
        if (add1!!.length < 6) {
            textAdd1.error = "Enter a valid address"
            textAdd1.requestFocus()
            return result
        }
        return true
    }


    private fun initUI(v: View) {
        val categoryList = categoryList
        categoryAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, categoryList)
        val stateArray = resources.getStringArray(R.array.States)
        stateAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, stateArray)
        cityAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            getCityList(textState.text.toString())
        )
        textCategory.setAdapter(categoryAdapter)
        textState.setAdapter(stateAdapter)
        textCity.setAdapter(cityAdapter)
        textState.onItemClickListener = this
        v.findViewById<View>(R.id.btnSubmitDocDetails).setOnClickListener {
            category = textCategory.text.toString()
            userName = textUserName.text.toString()
            state = textState.text.toString()
            city = textCity.text.toString()
            add1 = textAdd1.text.toString()
            add2 = textAdd2.text.toString()
            if (verifyData()) {
                userData.name = userName
                userData.state = state
                userData.city = city
                userData.addLine1 = add1
                userData.addLine2 = add2
                userData.category = textCategory.text.toString()
                Log.d("phone", "onClick: " + userData.contact)
                viewModel?.selectItem(userData)
            }
        }
    }

    private val categoryList: ArrayList<String>
        get() {
            val category = ArrayList<String>()
            category.add("Individual")
            category.add("NGO/Trust/Organisation")
            category.add("Supplier")
            category.add("Doctor")
            category.add("Other")
            return category
        }

    private fun getCityList(state: String): Array<String> {
        return when (state) {
            "Andhra Pradesh" -> resources.getStringArray(R.array.AndhraPradesh)
            "Assam" -> resources.getStringArray(R.array.Assam)
            "Bihar" -> resources.getStringArray(R.array.Bihar)
            "Chhattisgarh" -> resources.getStringArray(R.array.Chhattisgarh)
            "Delhi" -> resources.getStringArray(R.array.Delhi)
            "Goa" -> resources.getStringArray(R.array.Goa)
            "Gujarat" -> resources.getStringArray(R.array.Gujarat)
            "Haryana" -> resources.getStringArray(R.array.Haryana)
            "Himachal Pradesh" -> resources.getStringArray(R.array.HimachalPradesh)
            "Jharkhand" -> resources.getStringArray(R.array.Jharkhand)
            "Karanatka" -> resources.getStringArray(R.array.Karanatka)
            "Kerla" -> resources.getStringArray(R.array.Kerla)
            "Madhya Pradesh" -> resources.getStringArray(R.array.MadhyaPradesh)
            "Maharashtra" -> resources.getStringArray(R.array.Maharashtra)
            "Odisha" -> resources.getStringArray(R.array.Odisha)
            "Punjab" -> resources.getStringArray(R.array.Punjab)
            "Rajasthan" -> resources.getStringArray(R.array.Rajasthan)
            "Tamil Nadu" -> resources.getStringArray(R.array.TamilNadu)
            "Telangana" -> resources.getStringArray(R.array.Telangana)
            "Uttar Pradesh" -> resources.getStringArray(R.array.UttarPradesh)
            "Uttrakhand" -> resources.getStringArray(R.array.Uttrakhand)
            "West Bengal" -> resources.getStringArray(R.array.WestBengal)
            else -> arrayOf("Select State")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 2) {
            if (resultCode != Activity.RESULT_CANCELED) {
                assert(intent != null)
                val path: String = intent!!.getStringExtra("path")!!
                if (path != "null") {
                    val f = File(path)
                    if (f.exists()) {
                        val bitmap: Bitmap = BitmapFactory.decodeFile(path)
                        imageView.setImageBitmap(bitmap)
                        uploadImage(Uri.fromFile(f))
                        Log.d("TAG", "onActivityResult: imageuri$path")
                    } else {
                        val imageUri: Uri = intent.getParcelableExtra("URI")!!
                        imageView.setImageURI(imageUri)
                        uploadImage(imageUri)
                        Log.d("TAG", "onActivityResult: imageuri$imageUri")
                    }
                }
            }
        }
    }

    private fun uploadImage(filePath: Uri?) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val storageReference: StorageReference = FirebaseStorage.getInstance().getReference()
            val ref: StorageReference = storageReference
                .child(
                    "ProfileImage/"
                            + userData.contact
                )

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                .addOnSuccessListener { taskSnapshot ->


                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            context,
                            "Image Uploaded!!",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .addOnFailureListener{
                    e->
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            context,
                            "Failed " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress: Double = (100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded "
                                + progress.toInt() + "%"
                    )
                }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        cityAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            getCityList(textState.text.toString())
        )
        cityAdapter.notifyDataSetChanged()
        textCity.setAdapter(cityAdapter)
    }
}



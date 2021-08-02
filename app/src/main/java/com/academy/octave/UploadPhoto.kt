package com.academy.octave

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.academy.octave.UploadPhoto
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.theartofdev.edmodo.cropper.CropImageView.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class UploadPhoto : AppCompatActivity(), OnSetImageUriCompleteListener,
    OnCropImageCompleteListener {
    private lateinit var mCropImageView: CropImageView
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_user_image)
        mCropImageView = findViewById(R.id.cropImageView)
        mCropImageView.setOnSetImageUriCompleteListener(this)
        mCropImageView.setOnCropImageCompleteListener(this)
        if (checkAndRequestPermissions()) {
            CropImage.startPickImageActivity(this)
        }
        val cancel = findViewById<Button>(R.id.cancel)
        cancel.setOnClickListener { finish() }
        val ok = findViewById<Button>(R.id.crop)
        ok.setOnClickListener { mCropImageView.getCroppedImageAsync() }
        val rotate = findViewById<ImageButton>(R.id.rotate)
        rotate.setOnClickListener { mCropImageView.rotateImage(90) }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val wtite =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val read =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("in fragment on request", "Permission callback called-------")
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            val perms: MutableMap<String, Int> = HashMap()
            // Initialize the map with both permissions
            perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
            perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
            perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
            // Fill with actual results from user
            if (grantResults.size > 0) {
                for (i in permissions.indices) perms[permissions[i]] = grantResults[i]
                // Check for both permissions
                if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(
                        "in fragment on request",
                        "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted"
                    )
                    CropImage.startPickImageActivity(this)

                    // process the normal flow
                    //else any one or both the permissions are not granted
                } else {
                    Log.d("in fragment on request", "Some permissions are not granted ask again ")
                    //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.CAMERA
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        showDialogOK(
                            "Camera and Storage Permission required for this app"
                        ) { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                DialogInterface.BUTTON_NEGATIVE -> {
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Go to settings and enable permissions",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        val mIntent = Intent()
                        setResult(RESULT_CANCELED, mIntent)
                        //                            //proceed with logic by disabling the related features or quit the app.
                    }
                }
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel") { dialog, which ->
                Toast.makeText(this@UploadPhoto, "Permission Required!", Toast.LENGTH_LONG).show()
                val mIntent = Intent()
                setResult(RESULT_CANCELED, mIntent)
            }
            .create()
            .show()
    }

    override fun onBackPressed() {
        val mIntent = Intent()
        setResult(RESULT_CANCELED, mIntent)
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
            && resultCode == RESULT_OK
        ) {
            val imageUri = CropImage.getPickImageResultUri(this, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                    )
                }
            } else {
                mCropImageView!!.setImageUriAsync(imageUri)
            }
        }
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception) {
        if (error == null) {
            Toast.makeText(applicationContext, "Image load successful", Toast.LENGTH_SHORT).show()
        } else {
            Log.e("AIC", "Failed to load image by URI", error)
            Toast.makeText(
                applicationContext,
                "Image load failed: " + error.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCropImageComplete(view: CropImageView, result: CropResult) {
        try {
            handleCropResult(result)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(IOException::class)
    private fun handleCropResult(result: CropResult) {
        if (result.error == null) {
            val intent = Intent()
            intent.putExtra("SAMPLE_SIZE", result.sampleSize)
            if (result.uri != null) {
                intent.putExtra("URI", result.uri)
            } else {
                val bitmap =
                    if (mCropImageView!!.cropShape == CropImageView.CropShape.RECTANGLE) CropImage.toOvalBitmap(
                        result.bitmap
                    ) else result.bitmap
                val f = savebitmap(bitmap)
                val path = f.absolutePath
                intent.putExtra("path", path)
            }
            setResult(2, intent)
            finish()
        } else {
            Log.e("AIC", "Failed to crop image", result.error)
            Toast.makeText(
                applicationContext,
                "Image crop failed: " + result.error.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
        finish()
    }

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 7
        @Throws(IOException::class)
        fun savebitmap(bmp: Bitmap): File {
            val bytes = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, bytes)
            val f = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "testImage.jpg"
            )
            val result = f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            fo.close()
            Log.d("SaveImage", "savebitmap: failed$result")
            return f
        }
    }
}
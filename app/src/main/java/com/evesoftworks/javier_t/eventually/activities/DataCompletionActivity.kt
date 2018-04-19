package com.evesoftworks.javier_t.eventually.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.utils.RequestCode
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_data_completion.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.data_completion_toolbar.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class DataCompletionActivity : AppCompatActivity(), View.OnClickListener {
    var selectedImageUri: Uri? = null
    lateinit var storageReference: StorageReference
    lateinit var bitmap: Bitmap
    var signalCode: Int = 0
    val defaultImageUri = "https://firebasestorage.googleapis.com/v0/b/evedb-98c72.appspot.com/o/usersprofilepics%2Fdefault.jpg?alt=media&token=6c76f406-4c97-4ba9-82da-1720639376d1"

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.data_completion_profile_pic -> {
                userHasPermissions()
            }

            R.id.fab_to_grid -> {
                completeProfile()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_completion)

        setSupportActionBar(completionToolbar)

        if (userComesFromGoogleSignIn()) {
            val bundle = intent.extras
            //val googleAccountDefaultPic = bundle.get("googleAcountDefaultPic") as Bitmap

        }

        data_completion_profile_pic.setOnClickListener(this)
        fab_to_grid.setOnClickListener(this)
    }

    private fun uploadImage() {
        storageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${FirebaseAuth.getInstance().currentUser!!.uid}")
        when (signalCode) {
            0 -> updateProfileRequest(Uri.parse(defaultImageUri), data_completion_name.text.toString())
            1 -> {
                storageReference.putBytes(convertBitmapToByteArray(bitmap)).addOnSuccessListener {
                    updateProfileRequest(it.downloadUrl, data_completion_name.text.toString())
                }
            }
            2 -> {
                storageReference.putFile(selectedImageUri!!).addOnSuccessListener {
                    updateProfileRequest(it.downloadUrl, data_completion_name.text.toString())
                }
            }
        }
    }

    private fun userComesFromGoogleSignIn(): Boolean {
        return intent.extras != null
    }

    private fun completeProfile() {
        if (data_completion_name.text.toString() == "" || data_completion_username.text.toString() == "") {
            data_completion_name.error = "Rellena los inputs con información válida, por favor"
            data_completion_username.error = "Rellena los inputs con información válida, por favor"
        } else {
            data_completion_name.error = null
            data_completion_username.error = null

            uploadImage()
        }
    }

    private fun updateProfileRequest(imageUrl: Uri?, displayName: String) {
        val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUrl)
                .setDisplayName(displayName)
                .build()

        FirebaseAuth.getInstance().currentUser!!.updateProfile(userProfileChangeRequest).addOnSuccessListener {
            goToGridSelectionActivity()
        }
    }

    private fun goToGridSelectionActivity() {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RequestCode.RC_CAMERA -> {
                    bitmap = data!!.extras.get("data") as Bitmap
                    signalCode = 1

                    bitmap.let {
                        data_completion_profile_pic.setImageBitmap(bitmap)
                    }
                }

                RequestCode.RC_GALLERY -> {
                    if (data != null) {
                        selectedImageUri = data.data
                        signalCode = 2

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                            data_completion_profile_pic.setImageBitmap(bitmap)
                        } catch (exception: IOException) {
                            exception.printStackTrace()
                        }
                    }
                }
            }
        }

    }

    private fun userHasPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), RequestCode.RC_PERMISSION_CAMERA)
            userHasPermissions()
        } else {
            showChooserDialog()
        }
    }

    private fun showChooserDialog() {
        val chooserDialog = AlertDialog.Builder(this)
        chooserDialog.setTitle("Escoge una opción")

        val chooserOptions = arrayOf("Hacer una foto", "Elegir una foto de la galería")

        chooserDialog.setItems(chooserOptions) { _, it ->
            when (it) {
                0 -> goToCamera()
                1 -> goToGallery()
            }
        }

        chooserDialog.show()
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun goToCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, RequestCode.RC_CAMERA)
    }

    private fun goToGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, RequestCode.RC_GALLERY)
    }
}

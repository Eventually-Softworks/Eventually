package com.evesoftworks.javier_t.eventually.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.utils.RequestCode
import com.google.android.gms.flags.IFlagProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_data_completion.*
import java.io.IOException

class DataCompletionActivity : AppCompatActivity(), View.OnClickListener {
    var selectedImageUri: Uri? = null
    lateinit var storageReference: StorageReference
    val defaultImageUri = "https://firebasestorage.googleapis.com/v0/b/evedb-98c72.appspot.com/o/usersprofilepics%2Fdefault.jpg?alt=media&token=6c76f406-4c97-4ba9-82da-1720639376d1"

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.data_completion_profile_pic -> {
                userHasPermissions()
            }

            R.id.fab_to_grid -> {
                register()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_completion)

        data_completion_profile_pic.setOnClickListener(this)
        fab_to_grid.setOnClickListener(this)
    }

    private fun uploadImage() {
        storageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${FirebaseAuth.getInstance().currentUser!!.uid}")

        selectedImageUri?.let {
            storageReference.putFile(it).addOnSuccessListener {
                val uriDownloaded = it.downloadUrl
                setProfilePictureToUser(uriDownloaded)
            }.addOnFailureListener({
                Snackbar.make(findViewById(R.id.data_completion), "Ha ocurrido un error al subir la imagen", Snackbar.LENGTH_LONG)
            })
        } ?: kotlin.run {
            setProfilePictureToUser(Uri.parse(defaultImageUri))
        }
    }

    private fun register() {
        val retrievedUserData = intent.extras

        val userEmail = retrievedUserData.getString("USER_EMAIL")
        val userPassword = retrievedUserData.getString("USER_PASSWORD")

        if (TextUtils.isEmpty(data_completion_name.text.toString()) || TextUtils.isEmpty(data_completion_username.text.toString())) {
            data_completion_name.error = "Rellena los inputs con información válida, por favor"
            data_completion_username.error = "Rellena los inputs con información válida, por favor"
        } else {
            data_completion_name.error = null
            data_completion_username.error = null

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword).addOnSuccessListener {
                setDisplayNameToUser(data_completion_name.text.toString())
                uploadImage()
                goToGridSelectionActivity()
            }
        }
    }

    private fun setProfilePictureToUser(imageUrl: Uri?) {
        val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUrl)
                .build()

        FirebaseAuth.getInstance().currentUser!!.updateProfile(userProfileChangeRequest)
    }

    private fun setDisplayNameToUser(displayName: String) {
        val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

        FirebaseAuth.getInstance().currentUser!!.updateProfile(userProfileChangeRequest)
    }

    private fun goToGridSelectionActivity() {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bitmap: Bitmap

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RequestCode.RC_CAMERA -> {
                    bitmap = data!!.extras.get("data") as Bitmap

                    bitmap.let {
                        data_completion_profile_pic.setImageBitmap(bitmap)
                    }
                }

                RequestCode.RC_GALLERY -> {
                    if (data != null) {
                        selectedImageUri = data.data

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

    private fun goToCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, RequestCode.RC_CAMERA)
    }

    private fun goToGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, RequestCode.RC_GALLERY)
    }
}

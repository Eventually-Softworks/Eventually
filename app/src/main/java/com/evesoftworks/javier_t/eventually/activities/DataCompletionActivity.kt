package com.evesoftworks.javier_t.eventually.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.constants.ContentsUri
import com.evesoftworks.javier_t.eventually.constants.RequestCode
import com.evesoftworks.javier_t.eventually.constants.SignalCode
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_data_completion.*
import kotlinx.android.synthetic.main.data_completion_toolbar.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class DataCompletionActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var storageReference: StorageReference
    lateinit var bitmap: Bitmap
    var signalCode: Int = SignalCode.SC_DEFAULT_PROFILE_PICTURE
    val defaultImageUri = ContentsUri.CU_STORAGE_DEFAULT_PROFILE_PICTURE
    var selectedImageUri: Uri? = null

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
            Picasso.get().load(FirebaseAuth.getInstance().currentUser!!.photoUrl).into(data_completion_profile_pic)
            data_completion_name.setText(FirebaseAuth.getInstance().currentUser!!.displayName)
        } else {
            data_completion_profile_pic.setImageResource(R.mipmap.default_pic)
        }

        data_completion_profile_pic.setOnClickListener(this)
        fab_to_grid.setOnClickListener(this)
    }

    private fun uploadImage() {
        storageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${FirebaseAuth.getInstance().currentUser!!.uid}")
        when (signalCode) {
            SignalCode.SC_DEFAULT_PROFILE_PICTURE -> {
                if (userComesFromGoogleSignIn()) {
                    bitmap = (data_completion_profile_pic.drawable as BitmapDrawable).bitmap

                    storageReference.putBytes(convertBitmapToByteArray(bitmap)).addOnSuccessListener {
                        updateProfileRequest(FirebaseAuth.getInstance().currentUser!!.photoUrl, data_completion_name.text.toString())
                    }
                } else {
                    storageReference.putFile(Uri.parse(defaultImageUri)).addOnSuccessListener {
                        updateProfileRequest(Uri.parse(defaultImageUri), data_completion_name.text.toString())
                    }
                }
            }

            SignalCode.SC_PROFILE_PICTURE_SET_WITH_CAMERA -> {
                storageReference.putBytes(convertBitmapToByteArray(bitmap)).addOnSuccessListener {
                    updateProfileRequest(it.uploadSessionUri, data_completion_name.text.toString())
                }
            }

            SignalCode.SC_PROFILE_PICTURE_SET_WITH_GALLERY -> {
                storageReference.putFile(selectedImageUri!!).addOnSuccessListener {
                    updateProfileRequest(it.uploadSessionUri, data_completion_name.text.toString())
                }
            }
        }
    }

    private fun userComesFromGoogleSignIn(): Boolean {
        return GoogleSignIn.getSignedInAccountFromIntent(intent) != null
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RequestCode.RC_CAMERA -> {
                    bitmap = data!!.extras.get("data") as Bitmap
                    signalCode = SignalCode.SC_PROFILE_PICTURE_SET_WITH_CAMERA

                    bitmap.let {
                        data_completion_profile_pic.setImageBitmap(bitmap)
                    }
                }

                RequestCode.RC_GALLERY -> {
                    if (data != null) {
                        selectedImageUri = data.data
                        signalCode = SignalCode.SC_PROFILE_PICTURE_SET_WITH_GALLERY

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
        } else {
            showChooserDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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

    private fun goToGridSelectionActivity() {
        val intent = Intent(this, GridSelectionActivity::class.java)

        if (this.intent.extras?.get("DYN_LINK") != null) {
            intent.putExtra("DYN_LINK", this.intent.extras.getString("DYN_LINK"))
        }

        intent.putExtra("DISPLAYNAME_TO_FIRESTORE", data_completion_name.text.toString())
        intent.putExtra("USERNAME_TO_FIRESTORE", data_completion_username.text.toString())
        startActivity(intent)
        finish()
    }
}

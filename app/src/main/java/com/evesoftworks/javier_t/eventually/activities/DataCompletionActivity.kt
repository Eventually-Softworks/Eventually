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
import android.view.View
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.utils.RequestCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_data_completion.*
import java.io.IOException


class DataCompletionActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var selectedImageUri: Uri

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.data_completion_profile_pic -> {
                userHasPermissions()
            }

            R.id.fab_to_grid -> {
                uploadImage()
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
        selectedImageUri.let {
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${FirebaseAuth.getInstance().currentUser!!.uid}")

            storageReference.putFile(it).addOnSuccessListener {
                goToGridSelectionActivity()
            }.addOnFailureListener({
                Snackbar.make(findViewById(R.id.data_completion), "Ha ocurrido un error al subir la imagen", Snackbar.LENGTH_LONG)
            })
        }
    }

    private fun goToGridSelectionActivity() {
        val intent = Intent(this, GridSelectionActivity::class.java)
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

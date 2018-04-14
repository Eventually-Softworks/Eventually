package com.evesoftworks.javier_t.eventually.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.ProgressBar
import android.widget.TabHost
import com.evesoftworks.javier_t.eventually.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_data_completion.*
import java.io.IOException

class DataCompletionActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var filePath: Uri

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.data_completion_profile_pic -> {
                chooseImage()
            }

            R.id.fab_to_grid -> {
                uploadImage()
//                goToGridSelectionActivity()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_completion)

        data_completion_profile_pic.setOnClickListener(this)
        fab_to_grid.setOnClickListener(this)
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

       if (resultCode == RESULT_OK && requestCode == 0 && data != null && data.data != null) {
           filePath = data.data

           try {
               val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
               data_completion_profile_pic.setImageBitmap(bitmap)
           } catch (exception: IOException) {
                exception.printStackTrace()
           }
       }
    }

    private fun uploadImage() {
        filePath.let {
            val progressBar = ProgressBar(this)
            progressBar.max = 100
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${FirebaseAuth.getInstance().currentUser!!.uid}")

            storageReference.putFile(filePath).addOnSuccessListener {
                progressBar.progress = 100
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
}

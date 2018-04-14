package com.evesoftworks.javier_t.eventually.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.TabHost
import com.evesoftworks.javier_t.eventually.R
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_data_completion.*
import java.io.IOException

class DataCompletionActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var filePath: Uri

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.data_completion_profile_pic -> {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_completion)
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
            val storageReference: StorageReference
        }
    }
}

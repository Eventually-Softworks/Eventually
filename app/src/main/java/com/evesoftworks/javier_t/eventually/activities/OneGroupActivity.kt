package com.evesoftworks.javier_t.eventually.activities

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.CheckableContactsAdapter
import com.evesoftworks.javier_t.eventually.constants.ContentsUri
import com.evesoftworks.javier_t.eventually.constants.CustomResultCode
import com.evesoftworks.javier_t.eventually.constants.RequestCode
import com.evesoftworks.javier_t.eventually.constants.SignalCode
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.evesoftworks.javier_t.eventually.utils.RecyclerItemDivider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_data_completion.*
import kotlinx.android.synthetic.main.activity_one_group.*
import kotlinx.android.synthetic.main.activity_user_selection.*
import kotlinx.android.synthetic.main.group_creation_toolbar.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class OneGroupActivity : AppCompatActivity(), View.OnClickListener, OnRetrieveFirebaseDataListener {
    lateinit var storageReference: StorageReference
    lateinit var bitmap: Bitmap
    lateinit var groupId: String
    var signalCode: Int = SignalCode.SC_DEFAULT_PROFILE_PICTURE
    val defaultImageUri = "https://firebasestorage.googleapis.com/v0/b/evedb-98c72.appspot.com/o/groupsphotos%2Fdefault.png?alt=media&token=61627350-da98-4324-ae2b-c2f412f672d6"
    var selectedImageUri: Uri? = null
    val db = FirebaseFirestore.getInstance()
    var suggestions: ArrayList<User> = ArrayList()
    var currentUserPreferences: ArrayList<String> = ArrayList()
    var currentContacts: ArrayList<String> = ArrayList()
    var coincidences: ArrayList<User> = ArrayList()
    val onRetrieveFirebaseDataListener = this
    var canCreateGroup: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_group)

        retrieveCurrentUserPreferences()
        groupId = intent.extras.getString("collectionId")

        setSupportActionBar(group_creation_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        creation_group_pic.setImageResource(R.mipmap.default_pic)

        creation_group_pic.setOnClickListener(this)
        fab_to_select_participants.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.creation_group_pic -> {
                userHasPermissions()
            }

            R.id.fab_to_select_participants -> {
                completeData()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return true
    }

    private fun createGroup() {
        val storageReference = FirebaseStorage.getInstance().reference.child("groupsphotos/$groupId")

        when (signalCode) {
            SignalCode.SC_PROFILE_PICTURE_SET_WITH_CAMERA -> {
                storageReference.putBytes(convertBitmapToByteArray(bitmap)).addOnSuccessListener {
                    goToUserSelection()
                }
            }

            SignalCode.SC_PROFILE_PICTURE_SET_WITH_GALLERY -> {
                storageReference.putFile(selectedImageUri!!).addOnSuccessListener {
                    goToUserSelection()
                }
            }

            else -> goToUserSelection()
        }
    }

    private fun goToUserSelection() {
        if (canCreateGroup) {
            val intent = Intent(this, UserSelectionActivity::class.java)
            intent.putExtra("prepareDocId", groupId)
            intent.putParcelableArrayListExtra("selectableUsers", coincidences)
            intent.putExtra("groupName", creation_group_name.text.toString())
            startActivityForResult(intent, 7)
        } else {
            noCoincidencesDialog()
        }
    }

    private fun completeData() {
        if (creation_group_name.text.toString() == "") {
            creation_group_name.error = "Rellena los inputs con información válida, por favor"
        } else {
            creation_group_name.error = null

            createGroup()
        }
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RequestCode.RC_CAMERA -> {
                    bitmap = data!!.extras.get("data") as Bitmap
                    signalCode = SignalCode.SC_PROFILE_PICTURE_SET_WITH_CAMERA

                    bitmap.let {
                        creation_group_pic.setImageBitmap(bitmap)
                    }
                }

                RequestCode.RC_GALLERY -> {
                    if (data != null) {
                        selectedImageUri = data.data
                        signalCode = SignalCode.SC_PROFILE_PICTURE_SET_WITH_GALLERY

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                            creation_group_pic.setImageBitmap(bitmap)
                        } catch (exception: IOException) {
                            exception.printStackTrace()
                        }
                    }
                }
            }
        }

        if (resultCode == CustomResultCode.GROUP_CREATED) {
            val returnIntent = Intent()
            setResult(CustomResultCode.GROUP_CREATED, returnIntent)
            finish()
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

    override fun onRetrieved() {
        for (singleUser in suggestions) {
            for (possibleCoincidence in singleUser.friends) {
                if (possibleCoincidence == FirebaseAuth.getInstance().currentUser!!.uid) {
                    for (finalCoincidence in currentContacts) {
                        if (finalCoincidence == singleUser.photoId) {
                            coincidences.add(singleUser)
                        }
                    }
                }
            }
        }

        if (coincidences.size == 0) {
            canCreateGroup = false
        }

    }

    private fun prepareUsers() {
        suggestions.clear()

        var coincidences = 0

        db.collection("Usuarios").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    if (document.id != FirebaseAuth.getInstance().currentUser!!.uid) {
                        val user: User = document.toObject(User::class.java)

                        for (i in 0 until currentUserPreferences.size) {
                            coincidences = 0

                            for (j in 0 until user.categories.size) {
                                if (currentUserPreferences.contains(user.categories[j])) {
                                    coincidences++
                                }
                            }
                        }

                        if (coincidences > 0) {
                            suggestions.add(user)
                        }
                    }
                }

                onRetrieveFirebaseDataListener.onRetrieved()
            }
        }
    }

    private fun retrieveCurrentUserPreferences() {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                currentUserPreferences = user!!.categories
                currentContacts = user.friends

                prepareUsers()
            }
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

    private fun noCoincidencesDialog() {
        android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.no_coincidences_title))
                .setMessage(getString(R.string.no_coincidences_text))
                .setPositiveButton(getString(R.string.logout_ok), { _, _ ->
                    finish()
                }).show()
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

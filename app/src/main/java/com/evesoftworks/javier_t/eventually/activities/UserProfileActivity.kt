package com.evesoftworks.javier_t.eventually.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.method.KeyListener
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.EventsAdapter
import com.evesoftworks.javier_t.eventually.constants.RequestCode
import com.evesoftworks.javier_t.eventually.constants.SignalCode
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_data_completion.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.edit_profile_toolbar.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserProfileActivity : AppCompatActivity(), OnRetrieveFirebaseDataListener {
    val db = FirebaseFirestore.getInstance()
    val onRetrieveFirebaseDataListener = this
    lateinit var bitmap: Bitmap
    var signalCode: Int = -1
    var selectedImageUri: Uri? = null
    var confirmedAssistanceEvents: ArrayList<Event> = ArrayList()
    lateinit var adapter: EventsAdapter
    var anUserPreferences: ArrayList<String> = ArrayList()
    var currentUserPreferences: ArrayList<String> = ArrayList()
    var anUserEventsAssisting: ArrayList<String> = ArrayList()
    var confirmedAssistanceEventsId: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        finish_editing_button.visibility = View.GONE
        adapter = EventsAdapter(confirmedAssistanceEvents)

        setSupportActionBar(profile_toolbar)
        profile_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        profile_toolbar.setNavigationOnClickListener { finish() }

        prepareUserProfile()

        disableControls()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                userIsEditing()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val retrievedUserData = intent.extras

        if (retrievedUserData.getParcelable<User>("aContact") == null) {
            menuInflater.inflate(R.menu.menu_edit_profile, menu)
            return true
        }

        return false
    }

    override fun onRetrieved() {
        val stringBuilder = StringBuilder()

        if (anUserPreferences.isNotEmpty()) {
            for (preference in anUserPreferences) {
                when (preference) {
                    anUserPreferences.last() -> stringBuilder.append(" y $preference")
                    anUserPreferences.first() -> stringBuilder.append(preference)
                    else -> stringBuilder.append(", $preference")
                }
            }

            confirmedAssistanceEventsId = anUserEventsAssisting
        } else {
            for (myPreference in currentUserPreferences) {
                when (myPreference) {
                    currentUserPreferences.last() -> stringBuilder.append(" y $myPreference")
                    currentUserPreferences.first() -> stringBuilder.append(myPreference)
                    else -> stringBuilder.append(", $myPreference")
                }
            }
        }

        preferences_title.text = stringBuilder.toString()

        getConfirmedAssistanceEvents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RequestCode.RC_CAMERA -> {
                    bitmap = data!!.extras.get("data") as Bitmap
                    signalCode = SignalCode.SC_PROFILE_PICTURE_SET_WITH_CAMERA

                    bitmap.let {
                        profile_my_pic.setImageBitmap(bitmap)
                    }
                }

                RequestCode.RC_GALLERY -> {
                    if (data != null) {
                        selectedImageUri = data.data
                        signalCode = SignalCode.SC_PROFILE_PICTURE_SET_WITH_GALLERY

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                            profile_my_pic.setImageBitmap(bitmap)
                        } catch (exception: IOException) {
                            exception.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun retrievePreferencesFromAnUser(displayName: String) {
        db.collection("Usuarios").whereEqualTo("displayName", displayName).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.documents[0].toObject(User::class.java)
                anUserPreferences = user!!.categories
                anUserEventsAssisting = user.eventsAssisting

                onRetrieveFirebaseDataListener.onRetrieved()
            }
        }
    }

    private fun prepareUserProfile() {
        val retrievedUserData = intent.extras

        if (retrievedUserData.getParcelable<User>("aContact") != null) {
            val userData = retrievedUserData.getParcelable<User>("aContact")
            val storageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${userData.photoId}")

            storageReference.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    Picasso.get().load(it.result).into(profile_my_pic)
                }
            }

            profile_toolbar.title = "Perfil de ${userData.displayName}"
            profile_my_name.setText(userData.displayName, TextView.BufferType.EDITABLE)
            profile_my_email.visibility = View.GONE
            profile_my_username.setText(userData.username, TextView.BufferType.EDITABLE)

            retrievePreferencesFromAnUser(userData.displayName)
        } else {
            val currentUserData = retrievedUserData.getStringArrayList("USERDATA")
            val storageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${FirebaseAuth.getInstance().currentUser!!.uid}")

            storageReference.downloadUrl.addOnCompleteListener {
                if (it.isSuccessful) {
                    Picasso.get().load(it.result).into(profile_my_pic)
                }
            }

            Picasso.get().load(FirebaseAuth.getInstance().currentUser!!.photoUrl).into(profile_my_pic)
            profile_my_name.setText(currentUserData[0], TextView.BufferType.EDITABLE)
            profile_my_email.setText(currentUserData[1], TextView.BufferType.EDITABLE)
            profile_my_username.setText(currentUserData[2], TextView.BufferType.EDITABLE)

            interested_button.visibility = View.GONE

            retrieveInfoAboutCurrentUsersEventsIdAndPreferences()
        }
    }

    private fun userIsEditing() {
        enableControls()
    }

    private fun enableControls() {
        profile_my_pic.alpha = 0.7f
        profile_my_pic.setOnClickListener { userHasPermissions() }

        profile_my_name.isEnabled = true
        profile_my_username.isEnabled = true
        profile_my_username.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }

        finish_editing_button.visibility = View.VISIBLE
        finish_editing_button.setOnClickListener { userFinishedEditing() }
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

    private fun userFinishedEditing() {
        if (profile_my_name.text.toString() == "" || profile_my_username.text.toString() == "") {
            profile_my_name.error = "Rellena los inputs con información válida, por favor"
            profile_my_username.error = "Rellena los inputs con información válida, por favor"
        } else {
            profile_my_name.error = null
            profile_my_username.error = null

            uploadImage()
        }
    }

    private fun uploadImage() {
        val storageReference = FirebaseStorage.getInstance().reference.child("usersprofilepics/${FirebaseAuth.getInstance().currentUser!!.uid}")
        when (signalCode) {
            SignalCode.SC_PROFILE_PICTURE_SET_WITH_CAMERA -> {
                storageReference.putBytes(convertBitmapToByteArray(bitmap)).addOnSuccessListener {
                    updateProfileRequest(it.uploadSessionUri, profile_my_name.text.toString(), profile_my_username.text.toString())
                }
            }

            SignalCode.SC_PROFILE_PICTURE_SET_WITH_GALLERY -> {
                storageReference.putFile(selectedImageUri!!).addOnSuccessListener {
                    updateProfileRequest(it.uploadSessionUri, profile_my_name.text.toString(), profile_my_username.text.toString())
                }
            }

            else -> {
                updateProfileRequest(FirebaseAuth.getInstance().currentUser!!.photoUrl, profile_my_name.text.toString(), profile_my_username.text.toString())
            }
        }
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun updateProfileRequest(imageUrl: Uri?, displayName: String, username: String) {
        val updateUserData = HashMap<String, Any>()
        updateUserData["displayName"] = displayName
        updateUserData["username"] = username

        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).update(updateUserData).addOnCompleteListener {
            if (it.isSuccessful) {
                val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                        .setPhotoUri(imageUrl)
                        .setDisplayName(displayName)
                        .build()

                FirebaseAuth.getInstance().currentUser!!.updateProfile(userProfileChangeRequest).addOnCompleteListener {
                    disableControls()
                }
            } else {
                Snackbar.make(findViewById(R.id.user_profile_layout), "No se ha podido actualizar tu perfil, prueba de nuevo", Snackbar.LENGTH_LONG).show()
                disableControls()
            }
        }
    }

    private fun disableControls() {
        profile_my_pic.alpha = 1f
        profile_my_pic.setOnClickListener(null)

        profile_my_name.isEnabled = false
        profile_my_username.isEnabled = false

        finish_editing_button.visibility = View.GONE
        finish_editing_button.setOnClickListener(null)
    }

    private fun getConfirmedAssistanceEvents() {
        db.collection("Eventos").orderBy("eventDate").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val geoPoint: GeoPoint? = document.getGeoPoint("latLng")
                    val eventDate: Date? = document.getDate("eventDate")
                    var latLng: LatLng? = null
                    var dateToString: String? = null
                    val tags: String = document.get("tags").toString()
                    val spanishLocale = Locale("es", "ES")
                    val simpleDateFormat = SimpleDateFormat("dd MMMM yyy HH:mm", spanishLocale)
                    simpleDateFormat.timeZone = TimeZone.getTimeZone("Europe/Madrid")

                    geoPoint?.let {
                        latLng = LatLng(it.latitude, it.longitude)
                    }

                    eventDate?.let {
                        dateToString = simpleDateFormat.format(it)
                    }

                    val event = Event(document.getString("eventId")!!, document.getString("category")!!, latLng!!, document.getString("name")!!, document.getString("description")!!, document.getString("placeId")!!, dateToString!!, tags.split(","))

                    for (assistingEventId in confirmedAssistanceEventsId) {
                        if (assistingEventId == event.eventId) {
                            confirmedAssistanceEvents.add(event)
                        }
                    }
                }

                confirmed_assistance_recycle.setHasFixedSize(true)

                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                confirmed_assistance_recycle.layoutManager = layoutManager

                confirmed_assistance_recycle.adapter = adapter
            }
        }
    }

    private fun retrieveInfoAboutCurrentUsersEventsIdAndPreferences() {
        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.toObject(User::class.java)
                confirmedAssistanceEventsId = user!!.eventsAssisting
                currentUserPreferences = user.categories

                onRetrieveFirebaseDataListener.onRetrieved()
            }
        }
    }
}

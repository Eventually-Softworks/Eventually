package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.adapters.EventsAdapter
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataListener
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.edit_profile_toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserProfileActivity : AppCompatActivity(), OnRetrieveFirebaseDataListener {
    val db = FirebaseFirestore.getInstance()
    val onRetrieveFirebaseDataListener = this
    var confirmedAssistanceEvents: ArrayList<Event> = ArrayList()
    lateinit var adapter: EventsAdapter
    var anUserPreferences: ArrayList<String> = ArrayList()
    var currentUserPreferences: ArrayList<String> = ArrayList()
    var anUserEventsAssisting: ArrayList<String> = ArrayList()
    var confirmedAssistanceEventsId: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        adapter = EventsAdapter(confirmedAssistanceEvents)

        setSupportActionBar(profile_toolbar)
        profile_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        profile_toolbar.setNavigationOnClickListener { finish() }

        prepareUserProfile()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
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

            Picasso.get().load(FirebaseAuth.getInstance().currentUser!!.photoUrl).into(profile_my_pic)
            profile_my_name.setText(currentUserData[0], TextView.BufferType.EDITABLE)
            profile_my_email.setText(currentUserData[1], TextView.BufferType.EDITABLE)
            profile_my_username.setText(currentUserData[2], TextView.BufferType.EDITABLE)

            interested_button.visibility = View.GONE

            retrieveInfoAboutCurrentUsersEventsIdAndPreferences()
        }
    }

    private fun userIsEditing() {

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

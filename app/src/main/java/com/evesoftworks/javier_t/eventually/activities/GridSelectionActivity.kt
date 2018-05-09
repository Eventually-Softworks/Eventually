package com.evesoftworks.javier_t.eventually.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TableRow
import android.widget.ToggleButton
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.constants.RequestCode
import com.evesoftworks.javier_t.eventually.dbmodel.Category
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_grid_selection.*
import kotlinx.android.synthetic.main.grid_toolbar.*

class GridSelectionActivity : AppCompatActivity(), View.OnClickListener {
    private var userPreferencesSelected: ArrayList<String> = ArrayList()
    private var arrayOfPreferences: ArrayList<String> = ArrayList()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onClick(view: View?) {
        val button: ToggleButton = view as ToggleButton

        if (button.isChecked) {
            userPreferencesSelected.add(button.textOn.toString())
        } else {
            userPreferencesSelected.remove(button.textOn.toString())
        }

        if (enoughPreferences(userPreferencesSelected)) {
            continue_button.visibility = View.VISIBLE
        } else {
            continue_button.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_selection)
        setSupportActionBar(gridToolbar)
        continue_button.visibility = View.GONE

        setAllListeners()
        continue_button.setOnClickListener {
            userHasPermissions()
        }
    }

    private fun setAllListeners() {
        for (i in 0 until tableLayout.childCount) {
            val child: View = tableLayout.getChildAt(i)

            if (child is TableRow) {
                val row: TableRow = child

                for (j in 0 until row.childCount) {
                    val view: View = row.getChildAt(j)
                    view.setOnClickListener(this)
                }
            }
        }
    }

    private fun enoughPreferences(userPreferencesSelected: ArrayList<String>): Boolean {
        return userPreferencesSelected.size > 2
    }

    private fun userHasPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RequestCode.RC_PERMISSION_ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (i in 0 until userPreferencesSelected.size) {
                arrayOfPreferences.add(userPreferencesSelected[i])
            }

            val newUser = User(arrayOfPreferences, intent.extras.getString("DISPLAYNAME_TO_FIRESTORE"), ArrayList(), ArrayList(), intent.extras.getString("USERNAME_TO_FIRESTORE"), ArrayList(), ArrayList(), FirebaseAuth.getInstance().currentUser!!.uid)

            db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).set(newUser).addOnSuccessListener {
                goToMainPageActivity()
            }
        } else {
            userHasPermissions()
        }
    }

    private fun goToMainPageActivity() {
        val intent = Intent(applicationContext, MainPageActivity::class.java)

        if (this.intent.extras?.get("DYN_LINK") != null) {
            intent.putExtra("DYN_LINK", this.intent.extras.getString("DYN_LINK"))
        }

        startActivity(intent)
        finish()
    }
}
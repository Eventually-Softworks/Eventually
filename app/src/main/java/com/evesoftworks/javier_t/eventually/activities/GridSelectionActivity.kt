package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.dbmodel.Category
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.dbmodel.Group
import com.evesoftworks.javier_t.eventually.dbmodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_grid_selection.*
import kotlinx.android.synthetic.main.grid_toolbar.*

class GridSelectionActivity : AppCompatActivity(), View.OnClickListener {
    private var userPreferencesSelected: ArrayList<String> = ArrayList()
    private var arrayOfPreferences: ArrayList<Category> = ArrayList()
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

    private var continueButtonListener: View.OnClickListener = View.OnClickListener {
        for (i in 0 until userPreferencesSelected.size) {
            val actualPreference = Category(userPreferencesSelected[i])
            arrayOfPreferences.add(actualPreference)
        }

        val newUser = User(arrayOfPreferences, ArrayList(), intent.extras.get("USERNAME_TO_FIRESTORE") as String, ArrayList(), ArrayList())

        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).set(newUser)
        val intent = Intent(applicationContext, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_selection)
        setSupportActionBar(gridToolbar)
        continue_button.visibility = View.GONE

        setAllListeners()
        continue_button.setOnClickListener(continueButtonListener)
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
}

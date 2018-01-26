package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.view.View
import android.widget.*
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.databaseobjects.Category
import com.evesoftworks.javier_t.eventually.databaseobjects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_grid_selection.*

class GridSelectionActivity : AppCompatActivity(), View.OnClickListener {
    private var userPreferencesSelected: ArrayList<String> = ArrayList<String>()
    private var arrayOfPreferences: ArrayList<Category> = ArrayList<Category>()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onClick(view: View?) {
        val button: ToggleButton = view as ToggleButton

        if (button.isChecked) {
            Toast.makeText(this, button.textOn.toString(), Toast.LENGTH_LONG).show()
            userPreferencesSelected.add(button.textOn.toString())
        } else {
            userPreferencesSelected.remove(button.textOn.toString())
        }

        if (enoughPreferences(userPreferencesSelected)) {
            continue_button.visibility = View.VISIBLE
        } else {
            continue_button.visibility = View.GONE
        }
        Log.d("ARRAY", "${userPreferencesSelected.size}")
    }

    private var continueButtonListener: View.OnClickListener = object: View.OnClickListener {
        override fun onClick(view: View?) {
            for (i in 0 until userPreferencesSelected.size) {
                val actualPreference = Category(i+1, userPreferencesSelected[i])
                arrayOfPreferences.add(actualPreference)
            }

            val newUser = User(arrayOfPreferences)

            db.collection("Usuarios").add(newUser)
            val intent = Intent(applicationContext, MainPageActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_selection)
        supportActionBar?.title = "Selecciona tus gustos"
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

package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_grid_selection.*

class GridSelectionActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(view: View?) {
        Toast.makeText(this, "hola", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_selection)
        supportActionBar?.hide()

        addListenersToElements(gridLayout)
    }

    private fun addListenersToElements(mainGrid: GridLayout) {
        for (i in 0 until mainGrid.childCount) {
            val cardView: CardView = mainGrid.getChildAt(i) as CardView
            cardView.setOnClickListener(this)
        }
    }
}

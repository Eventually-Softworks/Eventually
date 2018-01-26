package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.evesoftworks.javier_t.eventually.R
import kotlinx.android.synthetic.main.activity_grid_selection.*

class GridSelectionActivity : AppCompatActivity(), View.OnClickListener {
    private var userPreferencesSelected: ArrayList<String> = ArrayList<String>()

    override fun onClick(view: View?) {
        val button: ToggleButton = view as ToggleButton

        if (button.isChecked) {
            Toast.makeText(this, button.textOn.toString(), Toast.LENGTH_LONG).show()
            userPreferencesSelected.add(button.textOn.toString())
        } else {
            userPreferencesSelected.remove(button.textOn.toString())
        }

        Log.d("ARRAY", "${userPreferencesSelected.size}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_selection)
        supportActionBar?.title = "Selecciona tus gustos"

        setAllListeners()

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
}

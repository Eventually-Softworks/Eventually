package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R

class CompleteEventsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_events_list)

        if (intent.extras?.getString("aCompleteEvent") != null) {
            Toast.makeText(this, intent.extras?.getString("aCompleteEvent"), Toast.LENGTH_LONG).show()
        }
    }
}

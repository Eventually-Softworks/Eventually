package com.example.javier_t.eventuallytest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.evesoftworks.javier_t.eventually.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_grid_selection.*

class GridSelectionActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_selection)
    }
}

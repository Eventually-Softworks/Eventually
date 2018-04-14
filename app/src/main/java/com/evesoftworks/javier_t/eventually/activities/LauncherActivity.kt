package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.evesoftworks.javier_t.eventually.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        userIsHere()
    }

    private fun userAlreadyHasProfile() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        db.collection("PreferenciasUsuario").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                goToMainPageActivity()
            } else {
                goToDataCompletionActivity()
            }
        }
    }

    private fun userIsHere() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            userAlreadyHasProfile()
        } else {
            goToSignInActivity()
        }
    }

    private fun goToMainPageActivity() {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToDataCompletionActivity() {
        val intent = Intent(this, DataCompletionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToSignInActivity() {
        val intent = Intent(this, DataCompletionActivity::class.java)
        startActivity(intent)
        finish()
    }
}
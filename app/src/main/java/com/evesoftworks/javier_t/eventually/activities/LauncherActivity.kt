package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.dbmodel.Event
import com.evesoftworks.javier_t.eventually.interfaces.OnRetrieveFirebaseDataWithArgsListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LauncherActivity : AppCompatActivity(), OnRetrieveFirebaseDataWithArgsListener {
    val onRetrieveFirebaseDataWithArgsListener = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        userComesFromDynamicLink()
    }

    override fun onRetrieve(args: String?, arrayList: ArrayList<Event>?) {
        val db = FirebaseFirestore.getInstance()

        if (FirebaseAuth.getInstance().currentUser != null) {
            if (FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
                db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        val intent = Intent(this, MainPageActivity::class.java)
                        intent.putExtra("DYN_LINK", args)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this, DataCompletionActivity::class.java)
                        intent.putExtra("DYN_LINK", args)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, SignInActivity::class.java)
                intent.putExtra("DYN_LINK", args)
                startActivity(intent)
                finish()
            }
        } else {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("DYN_LINK", args)
            startActivity(intent)
            finish()
        }
    }

    private fun userAlreadyHasProfile() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        db.collection("Usuarios").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                goToMainPageActivity()
            } else {
                goToDataCompletionActivity()
            }
        }
    }

    private fun userIsHere() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            if (!FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
                FirebaseAuth.getInstance().signOut()
                goToSignInActivity()
            } else {
                userAlreadyHasProfile()
            }
        } else {
            goToSignInActivity()
        }
    }

    private fun userComesFromDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                val eventId = it.result.link.lastPathSegment

                onRetrieveFirebaseDataWithArgsListener.onRetrieve(eventId)
            } else {
                userIsHere()
            }
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
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}

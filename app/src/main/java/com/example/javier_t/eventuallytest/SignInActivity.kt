package com.example.javier_t.eventuallytest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), View.OnClickListener, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {
    lateinit var mAuth: FirebaseAuth
    lateinit var gso: GoogleSignInOptions

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) Toast.makeText(this, "User signed in", Toast.LENGTH_LONG).show()
        else Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.register_text -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            R.id.sign_in_button -> {
                signIn(et_name.text.toString(), et_pass.text.toString())
            }

            R.id.google_sign_in_button -> {

            }
        }

    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        val user: FirebaseUser? = p0.currentUser

        if (user != null) {
            Toast.makeText(applicationContext, "User ${user.displayName} has signed in", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, "User ${user?.displayName} has signed out", Toast.LENGTH_LONG).show()
        }
    }

    private fun signIn(mail: String, password: String) {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        register_text.setOnClickListener(this)
        sign_in_button.setOnClickListener(this)
        google_sign_in_button.setOnClickListener(this)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mAuth = FirebaseAuth.getInstance()
        mAuth.addAuthStateListener(this)

        onAuthStateChanged(mAuth)
    }
}
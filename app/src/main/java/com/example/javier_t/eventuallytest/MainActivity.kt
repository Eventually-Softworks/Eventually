package com.example.javier_t.eventuallytest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.register_button -> {
                var regEmail: String = et_name.text.toString()
                var regPass: String = et_pass.text.toString()

                signIn(regEmail, regPass)
            }

            R.id.sign_in_button -> {
                var signEmail: String = et_name.text.toString()
                var signPass: String = et_pass.text.toString()

                register(signEmail, signPass)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button.setOnClickListener(this)
        sign_in_button.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = object: FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user: FirebaseUser? = p0.currentUser

                if (user != null) {
                    Toast.makeText(applicationContext, "User ${user.uid} has signed in", Toast.LENGTH_LONG).show()
                    /*val intent = Intent(this@MainActivity, SuccessActivity::class.java)

                    startActivity(intent)*/

                } else {
                    Toast.makeText(applicationContext, "User ${user?.uid} has signed out", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

    private fun register(mail: String, pass: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(object: OnCompleteListener<AuthResult> {
            override fun onComplete(p0: Task<AuthResult>) {
                if (p0.isSuccessful) {
                    Toast.makeText(applicationContext, "User created succesfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, p0.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun signIn(mail: String, pass: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass).addOnCompleteListener(object: OnCompleteListener<AuthResult> {
            override fun onComplete(p0: Task<AuthResult>) {
                if (p0.isSuccessful) {
                    Toast.makeText(applicationContext, "User signed succesfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, p0.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }

        })
    }
}


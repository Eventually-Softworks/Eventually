package com.example.javier_t.eventuallytest

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity(), View.OnClickListener, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {
    lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val intent = Intent(this, GridSelectionActivity::class.java)
            Toast.makeText(this, "User signed in", Toast.LENGTH_LONG).show()
            startActivity(intent)
        } else Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.register_text -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            R.id.sign_in_button -> {
                if (TextUtils.isEmpty(et_name.text.toString()) || TextUtils.isEmpty(et_pass.text.toString())) {
                    Toast.makeText(this, "Rellena los inputs con información válida, por favor", Toast.LENGTH_LONG).show()
                } else {
                    signIn(et_name.text.toString(), et_pass.text.toString())
                }

            }

            R.id.google_sign_in_button -> {
                signInGoogle()
            }
        }

    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        val user: FirebaseUser? = p0.currentUser

        if (user != null) {
            Toast.makeText(applicationContext, "User ${user.email} has signed in", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, "User ${user?.email} has signed out", Toast.LENGTH_LONG).show()
        }
    }

    private fun signIn(mail: String, password: String) {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(this)

    }

    private fun signInGoogle() {
        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent, 0)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, object:OnCompleteListener<AuthResult>{
            override fun onComplete(task: Task<AuthResult>) {
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    Toast.makeText(applicationContext, "has iniciado sesion con ${user?.email}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "pues falla como siempre", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        register_text.setOnClickListener(this)
        sign_in_button.setOnClickListener(this)
        google_sign_in_button.setOnClickListener(this)

        var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()
        mAuth.addAuthStateListener(this)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        Toast.makeText(this, "Hey ${currentUser}!", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount = task.getResult()
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(applicationContext, "1 FAIL FOR GRYFFINDOR", Toast.LENGTH_LONG).show()

            }
        }
    }
}
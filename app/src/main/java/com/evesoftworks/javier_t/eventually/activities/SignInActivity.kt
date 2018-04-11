package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*
import android.view.ViewGroup
import android.widget.Button
import com.crashlytics.android.Crashlytics
import com.google.firebase.firestore.FirebaseFirestore


class SignInActivity : AppCompatActivity(), View.OnClickListener, OnCompleteListener<AuthResult> {
    lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            updateUI()
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
                    et_name.error = "Rellena los inputs con información válida, por favor"
                    et_pass.error = "Rellena los inputs con información válida, por favor"
                } else {
                    et_name.error = null
                    et_pass.error = null
                    signIn(et_name.text.toString(), et_pass.text.toString())
                }

            }

            R.id.google_sign_in_button -> {
                signInGoogle()
            }
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

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
            override fun onComplete(task: Task<AuthResult>) {
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    Toast.makeText(applicationContext, "Has iniciado sesion con ${user?.email}", Toast.LENGTH_LONG).show()
                    updateUI()
                } else {
                    Toast.makeText(applicationContext, "Ha habido un error en el inicio de sesión", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()

        userIsHere()
        register_text.setOnClickListener(this)
        sign_in_button.setOnClickListener(this)
        google_sign_in_button.setOnClickListener(this)

        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(applicationContext, "Back pressed", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun updateUI(currentUser: FirebaseUser) {
        val intent = Intent(this, GridSelectionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun userIsHere() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun userAlreadySetPreferences(currentUser: FirebaseUser): Boolean {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    }
}
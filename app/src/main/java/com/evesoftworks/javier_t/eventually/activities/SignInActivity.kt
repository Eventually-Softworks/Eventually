package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.constants.RequestCode
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity(), View.OnClickListener, OnCompleteListener<AuthResult> {
    lateinit var mAuth: FirebaseAuth
    var mGoogleSignInClient: GoogleSignInClient? = null
    var mAccount: GoogleSignInAccount? = null

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            userAlreadySetPreferences(mAuth.currentUser!!)
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
            mAuth.signOut()
        }
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
        val intent = mGoogleSignInClient?.signInIntent
        startActivityForResult(intent, RequestCode.RC_GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userAlreadySetPreferences(FirebaseAuth.getInstance().currentUser!!)
            } else {
                Toast.makeText(applicationContext, "Ha habido un error en el inicio de sesión", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()

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
        if (requestCode == RequestCode.RC_GOOGLE_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                mAccount = task.getResult(ApiException::class.java)

                firebaseAuthWithGoogle(mAccount!!)
            } catch (e: ApiException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun userAlreadySetPreferences(currentUser: FirebaseUser) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        db.collection("Usuarios").document(currentUser.uid).get().addOnSuccessListener {
            if (it.exists()) {
                checkIfEmailVerified()
            } else {
                goToDataCompletionActivity()
            }
        }
    }

    private fun goToDataCompletionActivity() {
        val intent = Intent(this, DataCompletionActivity::class.java)

        if (mAccount != null) {
            intent.putExtra("googleAccountDefaultName", mAccount!!.displayName)
        }

        if (this.intent.extras?.get("DYN_LINK") != null) {
            intent.putExtra("DYN_LINK", this.intent.extras.getString("DYN_LINK"))
        }

        startActivity(intent)
        finish()
    }

    private fun goToMainPageActivity() {
        val intent = Intent(this, MainPageActivity::class.java)

        if (this.intent.extras?.get("DYN_LINK") != null) {
            intent.putExtra("DYN_LINK", this.intent.extras.getString("DYN_LINK"))
        }

        startActivity(intent)
        finish()
    }

    private fun showEmailVerificationDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.action_verification_email))
                .setMessage(getString(R.string.send_verification_email))
                .setPositiveButton(getString(R.string.logout_ok), { _, _ ->
                    checkIfEmailVerified()
                }).show()
    }

    private fun showEmailResendVerificationDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.action_verification_email))
                .setMessage(getString(R.string.resend_verification_email))
                .setPositiveButton(getString(R.string.verification_send), { _, _ ->
                    sendVerificationEmail()
                })
                .setNegativeButton(getString(R.string.logout_cancel), null).show()
    }

    private fun showEmailSendVerificationDialogIfUserIsNotVerified() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.action_verification_email))
                .setMessage(getString(R.string.send_verification_email_if_not_verified))
                .setPositiveButton(getString(R.string.verification_send_if_not_verified), { _, _ ->
                    sendVerificationEmail()
                }).show()
    }

    private fun checkIfEmailVerified() {
        val user = mAuth.currentUser

        user!!.reload().addOnSuccessListener {
            if (user.isEmailVerified) {
                goToMainPageActivity()
            } else {
                showEmailSendVerificationDialogIfUserIsNotVerified()
            }
        }
    }

    private fun sendVerificationEmail() {
        val user = mAuth.currentUser

        user!!.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful) {
                showEmailVerificationDialog()
            } else {
                showEmailResendVerificationDialog()
            }
        }
    }
}
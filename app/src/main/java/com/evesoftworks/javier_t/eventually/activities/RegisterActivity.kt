package com.evesoftworks.javier_t.eventually.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener, OnCompleteListener<AuthResult> {
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthStateLisener: FirebaseAuth.AuthStateListener
    var mGoogleSignInClient: GoogleSignInClient? = null


    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            sendVerificationEmail()
        } else {
            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()

            register_button.revertAnimation {
                register_button.background = getDrawable(R.drawable.rounded_button)
            }
        }
    }

    override fun onClick(elementPressed: View?) {
        when (elementPressed?.id) {
            R.id.register_button -> {
                if (TextUtils.isEmpty(et_name.text.toString()) || TextUtils.isEmpty(et_pass.text.toString()) || TextUtils.isEmpty(et_repass.text.toString())) {
                    et_name.error = "Rellena los inputs con información válida, por favor"
                    et_pass.error = "Rellena los inputs con información válida, por favor"
                    et_repass.error = "Rellena los inputs con información válida, por favor"
                } else {
                    et_name.error = null
                    et_pass.error = null
                    et_repass.error = null

                    if (et_pass.text.toString() == et_repass.text.toString()) {
                        register(et_name.text.toString(), et_pass.text.toString())
                    } else {
                        et_pass.error = "Las contraseñas no coinciden, revisalas, por favor"
                        et_repass.error = "Las contraseñas no coinciden, revisalas, por favor"
                    }
                }
            }

            R.id.register_text -> {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        register_button.background = getDrawable(R.drawable.rounded_button)
        mAuth = FirebaseAuth.getInstance()
        register_button.setOnClickListener(this)
        register_text.setOnClickListener(this)

        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuthStateLisener = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null) {
                sendVerificationEmail()
            }
        }
    }

    private fun register(mail: String, password: String) {
        register_button.startAnimation()
        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(this)
    }

    private fun goToDataCompletionActivity() {
        val intent = Intent(this, DataCompletionActivity::class.java)
        intent.putExtra("USER_EMAIL", et_name.text.toString())
        intent.putExtra("USER_PASSWORD", et_pass.toString())
        startActivity(intent)
        finish()
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
                register_button.doneLoadingAnimation(ContextCompat.getColor(this, R.color.colorPrimary), BitmapFactory.decodeResource(resources, R.drawable.ic_check_white_24dp))
                goToDataCompletionActivity()
            } else {
                showEmailSendVerificationDialogIfUserIsNotVerified()
            }
        }
    }
}

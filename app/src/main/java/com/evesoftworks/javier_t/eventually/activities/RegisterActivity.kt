package com.evesoftworks.javier_t.eventually.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.evesoftworks.javier_t.eventually.R
import com.evesoftworks.javier_t.eventually.databaseobjects.Location
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener, OnCompleteListener<AuthResult> {
    lateinit var mAuth: FirebaseAuth

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            Toast.makeText(this, "Registration ended successfully", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(elementPressed: View?) {
        when (elementPressed?.id) {
            R.id.register_button -> {
                if (TextUtils.isEmpty(et_name.text.toString()) || TextUtils.isEmpty(et_pass.text.toString()) || TextUtils.isEmpty(et_repass.text.toString())) {
                    Toast.makeText(this, "Rellena los inputs con información válida, por favor", Toast.LENGTH_LONG).show()
                } else {
                    if (et_pass.text.toString().equals(et_repass.text.toString())) {
                        register(et_name.text.toString(), et_pass.text.toString())
                        finish()
                    } else {
                        Toast.makeText(this, "Las contraseñas no coinciden, revísalas, por favor", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        register_button.setOnClickListener(this)

    }

    private fun register(mail: String, password: String) {
        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(this)
    }
}

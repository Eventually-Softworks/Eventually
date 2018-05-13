package com.evesoftworks.javier_t.eventually.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.evesoftworks.javier_t.eventually.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.feedback_toolbar.*
import java.util.*


class FeedbackActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var choice: String

    override fun onClick(v: View?) {
        if (comment_feedback.text.toString() == "") {
            comment_feedback.error = "Rellena los inputs con información válida, por favor"
        } else {
            comment_feedback.error = null
            choice = spinner_feedback.selectedItem as String
            submitFeedback()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        setSupportActionBar(feedback_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        send_feedback_button.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return true
    }

    private fun submitFeedback() {
        val string = "Mensaje de ${FirebaseAuth.getInstance().currentUser!!.uid} con motivo $choice ---> ${comment_feedback.text}"
        val feedbackData = HashMap<String, Any>()
        feedbackData["message"] = string

        FirebaseFirestore.getInstance().collection("Feedback").document(Calendar.getInstance().timeInMillis.toString()).set(feedbackData).addOnCompleteListener {
            if (it.isSuccessful) {
                finish()
            } else {
                Snackbar.make(findViewById(R.id.feedback_act), "No se ha podido enviar el mensaje, comprueba tu conexión y vuelve a intentarlo", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

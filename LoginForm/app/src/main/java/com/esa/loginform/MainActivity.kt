package com.esa.loginform

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton

const val VALIDATION_INTENT = 1
const val USERNAME_KEY = "USERNAME_KEY"
const val PASSWORD_KEY = "PASSWORD_KEY"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val login = findViewById<MaterialButton>(R.id.login)
        login.setOnClickListener {
            val username = findViewById<EditText>(R.id.username).text.toString().trim()
            val password = findViewById<EditText>(R.id.password).text.toString().trim()

            Intent(this, ValidationActivity::class.java).also { validationIntent ->
                validationIntent.putExtra(USERNAME_KEY, username)
                validationIntent.putExtra(PASSWORD_KEY, password)
                startActivityForResult(validationIntent, VALIDATION_INTENT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VALIDATION_INTENT && resultCode == Activity.RESULT_OK) {
            findViewById<TextView>(R.id.title).visibility = View.GONE
            val successfulLogin = findViewById<TextView>(R.id.successful_login)
            successfulLogin.visibility = View.VISIBLE
            successfulLogin.text = getString(R.string.successful_login, findViewById<EditText>(R.id.username).text)
            findViewById<EditText>(R.id.username).visibility = View.GONE
            findViewById<EditText>(R.id.password).visibility = View.GONE
            findViewById<MaterialButton>(R.id.login).visibility = View.GONE
        } else if (requestCode == VALIDATION_INTENT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.not_successful_login), Toast.LENGTH_LONG).show()
        }
    }
}

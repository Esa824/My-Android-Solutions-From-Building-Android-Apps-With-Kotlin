package com.esa.loginform

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ValidationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var successful = 0
        //Get the intent which started this activity
        intent?.let {

            //Set the welcome message
            val username = it.getStringExtra(USERNAME_KEY)
            val password = it.getStringExtra(PASSWORD_KEY)

            if (username == "Esa" && password == "Esa") {
                successful = -1
            }
        }
        Intent().let { validationIntent ->
            setResult(successful,validationIntent)
            finish()
        }
    }
}
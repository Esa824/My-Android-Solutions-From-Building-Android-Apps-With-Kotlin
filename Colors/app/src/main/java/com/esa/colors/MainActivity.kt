package com.esa.colors

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.enter_button)?.setOnClickListener {
            //Get the red_channel TextInputEditText value
            val redChannel = findViewById<TextInputEditText>(R.id.red_channel)?.text.toString().trim()

            //Get the green_channel TextInputEditText value
            val greenChannel = findViewById<TextInputEditText>(R.id.green_channel)?.text.toString().trim()

            //Get the green_channel TextInputEditText value
            val blueChannel = findViewById<TextInputEditText>(R.id.blue_channel)?.text.toString().trim()

            //Check names are not empty here:
            if (redChannel.isNotEmpty() && greenChannel.isNotEmpty() && blueChannel.isNotEmpty()) {
                val hexColor = redChannel + greenChannel + blueChannel
                // Set the text color of the TextView
                val RgbColor = findViewById<TextView>(R.id.rgbcolor)
                val color = Color.parseColor("#$hexColor")
                RgbColor.setBackgroundColor(color)
                Toast.makeText(this, getString(R.string.parameters_empty), Toast.LENGTH_LONG)
                    .apply {
                        setGravity(Gravity.CENTER, 0, 0)
                        show()
                    }
            }
        }
        findViewById<Button>(R.id.exit_button)?.setOnClickListener {
            finish();
        }
    }
}
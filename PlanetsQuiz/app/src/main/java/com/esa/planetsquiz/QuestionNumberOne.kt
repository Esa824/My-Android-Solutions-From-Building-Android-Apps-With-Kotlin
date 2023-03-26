package com.esa.planetsquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textview.MaterialTextView

class QuestionNumberOne : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.planets)
        findViewById<TextView>(R.id.question).isVisible = true
        findViewById<TextView>(R.id.question).text = getString(R.string.question_number_one)
        val intent = Intent()
        for (planetButtonId in listOf(R.id.mercury, R.id.venus, R.id.earth, R.id.mars, R.id.jupiter, R.id.saturn, R.id.uranus, R.id.neptune)) {
            setupPlanetButtonClickListener(planetButtonId, intent)
        }
    }

    private fun setupPlanetButtonClickListener(buttonId: Int, intent: Intent) {
        findViewById<MaterialTextView>(buttonId).setOnClickListener {
            if (buttonId == R.id.jupiter) {
                findViewById<TextView>(R.id.answer).text = "CORRECT!".plus(" ").plus(getString(R.string.answer_question_number_one))
                setResult(Activity.RESULT_OK, intent)
            } else {
                findViewById<TextView>(R.id.answer).text = "WRONG!".plus(" ").plus(getString(R.string.answer_question_number_one))
                setResult(Activity.RESULT_CANCELED, intent)
            }
        }
    }
}

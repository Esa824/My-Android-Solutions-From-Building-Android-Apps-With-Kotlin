package com.esa.planetsquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textview.MaterialTextView

class QuestionNumberTwo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent()
        setContentView(R.layout.planets)
        findViewById<TextView>(R.id.question).isVisible = true
        findViewById<TextView>(R.id.question).text = getString(R.string.question_number_two)
        for (planetButtonId in listOf(R.id.mercury, R.id.venus, R.id.earth, R.id.mars, R.id.jupiter, R.id.saturn, R.id.uranus, R.id.neptune)) {
            setupPlanetButtonClickListener(planetButtonId, intent)
        }
    }

    private fun setupPlanetButtonClickListener(buttonId: Int, intent: Intent) {
        findViewById<MaterialTextView>(buttonId).setOnClickListener {
            if (buttonId == R.id.saturn) {
                findViewById<TextView>(R.id.answer).text = "CORRECT!".plus(" ").plus(getString(R.string.answer_question_number_two))
                setResult(Activity.RESULT_OK, intent)
            } else {
                findViewById<TextView>(R.id.answer).text = "WRONG!".plus(" ").plus(getString(R.string.answer_question_number_two))
                setResult(Activity.RESULT_CANCELED, intent)
            }
        }
    }
}
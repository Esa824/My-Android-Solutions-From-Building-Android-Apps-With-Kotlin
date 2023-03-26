package com.esa.planetsquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val questionNumberOne = findViewById<Button>(R.id.question_number_one)
        val questionNumberTwo = findViewById<Button>(R.id.question_number_two)
        val questionNumberThree = findViewById<Button>(R.id.question_number_three)

        questionNumberOne.setOnClickListener {
            val intent = Intent(this, QuestionNumberOne::class.java)
            startActivityForResult(intent, 0)
        }
        questionNumberTwo.setOnClickListener {
            val intent = Intent(this, QuestionNumberTwo::class.java)
            startActivityForResult(intent, 0)
        }
        questionNumberThree.setOnClickListener {
            val intent = Intent(this, QuestionNumberThree::class.java)
            startActivityForResult(intent, 0)
        }
    }
}
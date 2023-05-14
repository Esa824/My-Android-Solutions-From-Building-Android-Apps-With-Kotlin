package com.esa.mywatertracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.esa.mywatertracker.WaterTrackingService.Companion.EXTRA_INTAKE_AMOUNT_MILLILITERS

class MainActivity : AppCompatActivity() {
    private val waterButton: View
            by lazy { findViewById(R.id.drink_water) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        launchTrackingService()

        waterButton.setOnClickListener {
            launchTrackingService(250f)
        }
    }

    private fun launchTrackingService(amount : Float = 0f) {
        val serviceIntent = Intent(this, WaterTrackingService::class.java).apply {
            putExtra(EXTRA_INTAKE_AMOUNT_MILLILITERS, amount)
        }
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}
package com.esa.mywatertracker

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class WaterTrackingService : Service() {

    private var waterLevelMilliliters = 0f
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var serviceHandler: Handler

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        notificationBuilder = createNotificationBuilder()
        val handlerThread = HandlerThread("WaterTrackingThread").apply { start() }
        serviceHandler = Handler(handlerThread.looper)
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        updateFluidBalance()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val returnValue = super.onStartCommand(intent, flags, startId)

        val intakeAmountMilliliters = intent?.getFloatExtra(EXTRA_INTAKE_AMOUNT_MILLILITERS, 0f)
        if (intakeAmountMilliliters != null && intakeAmountMilliliters > 0) {
            addToFluidBalance(intakeAmountMilliliters)
        }

        return returnValue
    }

    override fun onDestroy() {
        serviceHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_MUTABLE
        )

        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        } else {
            ""
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking your water level")
            .setContentText("Your water level: %.2f".format(waterLevelMilliliters))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker("Water tracking started")
    }

    private fun addToFluidBalance(amount: Float) {
        synchronized(this) {
            if (amount == -0.1f && waterLevelMilliliters == 0.0f) {
                return
            }
            waterLevelMilliliters += amount
        }
    }

    private fun updateFluidBalance() {
        serviceHandler.postDelayed({
            addToFluidBalance(-0.1f)
            notificationBuilder.setContentText(
                "Your water level: %.2f".format(waterLevelMilliliters)
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            updateFluidBalance()
        }, 5000L)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "waterTracking"
        val channelName = "Water Tracking"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    companion object {
        const val EXTRA_INTAKE_AMOUNT_MILLILITERS = "intake"
        private const val NOTIFICATION_ID = 0x3A7A
    }
}

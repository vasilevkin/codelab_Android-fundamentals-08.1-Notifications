package com.vasilevkin.notifyme

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {
    private lateinit var buttonNotify: Button
    private lateinit var buttonCancel: Button
    private lateinit var buttonUpdate: Button

    private var mNotifyManager: NotificationManager? = null
    private val mReceiver = NotificationReceiver()

    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private val NOTIFICATION_ID = 0
    private val ACTION_UPDATE_NOTIFICATION =
        "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION"
    private val ACTION_DELETE_NOTIFICATION =
        "com.example.android.notifyme.ACTION_DELETE_NOTIFICATION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonNotify = findViewById(R.id.notify)
        buttonNotify.setOnClickListener { sendNotification() }

        buttonCancel = findViewById(R.id.cancel)
        buttonCancel.setOnClickListener {
            //Cancel the notification
            cancelNotification()
        }

        buttonUpdate = findViewById(R.id.update)
        buttonUpdate.setOnClickListener {
            //Update the notification
            updateNotification()
        }

        createNotificationChannel()
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )

        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    fun sendNotification() {
        Toast.makeText(
            getApplicationContext(),
            "Notification is sent",
            Toast.LENGTH_LONG
        ).show()

        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            updateIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
        mNotifyManager?.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = true,
            isCancelEnabled = true
        )
    }

    fun updateNotification() {
        Toast.makeText(
            applicationContext,
            "Notification is Updated",
            Toast.LENGTH_LONG
        ).show()

        val androidImage = BitmapFactory
            .decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder = getNotificationBuilder()

        notifyBuilder.setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!")
        )

        mNotifyManager?.notify(NOTIFICATION_ID, notifyBuilder.build())

        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = false,
            isCancelEnabled = true
        )
    }

    fun cancelNotification() {
        Toast.makeText(
            applicationContext,
            "Notification is Cancelled",
            Toast.LENGTH_LONG
        ).show()

        mNotifyManager?.cancel(NOTIFICATION_ID)

        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
    }

    fun createNotificationChannel() {
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Mascot Notification", NotificationManager
                    .IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from Mascot"
            mNotifyManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val cancelIntent = Intent(this, MainActivity::class.java)
//            Intent(ACTION_DELETE_NOTIFICATION)
        cancelIntent.action = "notification_cancelled"
        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            cancelIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

//        PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("You've been notified!")
            .setContentText("This is your notification text.")
            .setSmallIcon(R.drawable.ic_android)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setDeleteIntent(cancelPendingIntent)
    }

    fun setNotificationButtonState(
        isNotifyEnabled: Boolean,
        isUpdateEnabled: Boolean,
        isCancelEnabled: Boolean
    ) {
        buttonNotify.isEnabled = isNotifyEnabled
        buttonUpdate.isEnabled = isUpdateEnabled
        buttonCancel.isEnabled = isCancelEnabled
    }


    inner class NotificationReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val action = intent.action
            Log.w("NotifyMe","intent action = $action")

            if (action == "notification_cancelled") {
                Log.w("NotifyMe","intent notification_cancelled is received")
                // your code
                cancelNotification()
            }

            // Update the notification
            updateNotification()
        }
    }
}

package com.xeatpos.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.xeatpos.R
import com.xeatpos.activities.HomeActivity
import com.xeatpos.utils.Constants
import java.util.*


class MyFCMService : FirebaseMessagingService() {
    var flag = false
    private val TAG = "Notification"
    private lateinit var notificationManager: NotificationManager
    //private val ADMIN_CHANNEL_ID = "Driver"
    private val ADMIN_CHANNEL_ID = "Xeat poss"
    var pendingIntent: PendingIntent? = null
    lateinit var soundUri: Uri

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    @SuppressLint("InvalidWakeLockTag")
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        Log.i("### msg : ", p0.toString())

        flag=false
        p0.let { message ->
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            val isScreenOn = pm.isScreenOn
            if (isScreenOn == false) {
                val wl = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                    "MyLock"
                )
                wl.acquire(10000)
                val wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock")
                wl_cpu.acquire(10000)
            }
            Log.e(TAG, message.getData().toString())
            Log.e(TAG, message.getData().get("title").toString())
            val t = message.getData().get("title").toString()
            val b = message.getData().get("body").toString()

            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            var sound = message.getData().get("sound").toString()
            Log.e("check", ""+sound)

//            soundUri =
//                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.old_telephone)

            if (t.equals("New Order Received")) {
                soundUri =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.old_telephone)
            } else if(t.equals("Advanced Order Received")) {
                soundUri =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.sch_noti)
            }
            else
            {
                soundUri =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.nsound)

            }
            val intent = Intent("myFunction")
            // add data
            // add datad
            intent.putExtra("value1", t)
            intent.putExtra("value2", "Testtttttttttttttttttt")
            if (flag == false) {
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                flag = true
            }
            val notificationId = Random().nextInt(60000)
            val notificationIntent = Intent(this@MyFCMService, HomeActivity::class.java)
            notificationIntent.putExtra("test", b)
            notificationIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            pendingIntent = PendingIntent.getActivity(
                this@MyFCMService, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE

            )
            val largeIcon =
                BitmapFactory.decodeResource(resources, R.drawable.ic_notifications)

            grantUriPermission(applicationContext.packageName, soundUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            var notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


               // val channelId = "custom_sound_channel"

                val channelName = "XeatPoss"

                    // var alarmSound: Uri? = null
               // alarmSound = if (message.data["title"] == "New Order Received") {
             //   var alarmSound: Uri? = Uri.parse("android.resource://" + packageName + "/" + R.raw.old_telephone)
              //  } else {
              //      Uri.parse("android. ://" + packageName + "/" + R.raw.nsound)
              //  }

                val channel =
                    NotificationChannel(ADMIN_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                channel.setSound(soundUri, audioAttributes)

                if (notificationManager.getNotificationChannel(ADMIN_CHANNEL_ID) == null) {
                    notificationManager.createNotificationChannel(channel)
                }
                Notification.Builder(this, ADMIN_CHANNEL_ID)
                    .setContentTitle(message.data["title"])
                    .setContentText(message.data["body"])
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                   // .setSound(soundUri, audioAttributes)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setLargeIcon(largeIcon)



            }
            else {

                Notification.Builder(this)
                    .setContentTitle(message.data["title"])
                    .setContentText(message.data["body"])
                    //     .setSound(soundUri)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setAutoCancel(true)
                    .setLargeIcon(largeIcon)
            }

            if (soundUri != null)
            {
                Constants.player_ring = RingtoneManager.getRingtone(applicationContext, soundUri)
                Constants.player_ring.play()
            }
            //  notification.flags |= Notification.FLAG_INSISTENT | Notification.FLAG_NO_CLEAR;
            //val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Since android Oreo notification channel is needed.
          //  val adminChannelName = "XeatPoss"
            // Since android Oreo notification channel is needed.

           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                val channel = NotificationChannel(
                    ADMIN_CHANNEL_ID,
                    adminChannelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
                //   channel.setSound(soundUri, audioAttributes);
                channel.setSound(null, null);
                notificationManager.createNotificationChannel(channel)
                notification.setChannelId(ADMIN_CHANNEL_ID);
            }*/
            notificationManager.notify(notificationId, notification.build())

            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }
        }
    }

    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .build()
        WorkManager.getInstance().beginWith(work).enqueue()
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }
}

package com.xeatpos.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.xeatpos.R;
import com.xeatpos.activities.HomeActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private boolean flag = false;

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        Log.i("### msg : ", msg.toString());

        flag = false;

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(isScreenOn==false)
        {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

            wl_cpu.acquire(10000);
        }

        if (msg.getData().size() > 0) {
            Log.i("### data : ", msg.getData().toString());
            sendTopNotification(msg.getData().get("title"), msg.getData().get("body"));
            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }
        }

        if (msg.getNotification() != null) {
            Log.i("### notification : ", msg.getNotification().getBody());
            sendTopNotification(msg.getNotification().getTitle(), msg.getNotification().getBody());
        }
    }

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendTopNotification(String title, String content) {

        Intent intent = new Intent("myFunction");
        // add data
        intent.putExtra("value1", title);
        intent.putExtra("value2", "Testtttttttttttttttttt");
        if(flag == false) {
            System.out.println("FCM Dismiss Dialog");

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            flag = true;

        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_DEFAULT_IMPORTANCE = "channel_id";
        final int ONGOING_NOTIFICATION_ID = 1;
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.putExtra("test",content);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "custom_sound_channel";

            String channelName = "ChannelPOS";

            Uri alarmSound = null;
            if(title.equals("New Order Received")){
                alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.old_telephone);
            }else{
                alarmSound = Uri.parse("android. ://" + getPackageName() + "/" + R.raw.nsound);
            }

             NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            // Set the custom sound and audio attributes for the channel
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            channel.setSound(alarmSound, audioAttributes);

            if (notificationManager.getNotificationChannel(channelId) == null) {
                notificationManager.createNotificationChannel(channel);
            }


            notification = new Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        else{
            Uri alarmSound = null;
            if(title.equals("New Order Received")){

                alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.old_telephone);

            }else{
                alarmSound = Uri.parse("android. ://" + getPackageName() + "/" + R.raw.nsound);
            }
            notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSound(alarmSound)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentIntent(pendingIntent)
                    .build();

        }

        if(title.equals("New Order Received")) {
            notification.flags |= Notification.FLAG_INSISTENT | Notification.FLAG_NO_CLEAR;
        }
        // Since android Oreo notification channel is needed.
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_DEFAULT_IMPORTANCE,
                    "ChannelPOS",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
*/
        notificationManager.notify(ONGOING_NOTIFICATION_ID, notification);
    }
}

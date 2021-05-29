package kz.yassy.taxi.common.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kz.yassy.taxi.R;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.ui.activity.main.MainActivity;

import static kz.yassy.taxi.common.Constants.BroadcastReceiver.INTENT_FILTER;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private int notificationId = 0;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        SharedHelper.putKey(this, "device_token", s);
        SharedHelper.putKey(this, "device_id", deviceId);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String chat = null;
        System.out.println("RRR FireBaseMessaging data payload: " + remoteMessage.getData());
        if (remoteMessage.getData().size() > 0) {
            try {
                chat = remoteMessage.getData().get("custom");
            } catch (Exception e) {
                e.printStackTrace();
            }
            openMainActivity(remoteMessage.getData().get("message"), !TextUtils.isEmpty(chat));
            sendBroadcast(new Intent(INTENT_FILTER));
        } else sendBroadcast(new Intent(INTENT_FILTER));
    }

    private void openMainActivity(String messageBody, boolean isChat) {
        System.out.println("RRR FireBaseMessaging messageBody = [" + messageBody + "], isChat = [" + isChat + "]");

//        MvpApplication.canGoToChatScreen = isChat;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity
                (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = String.valueOf(1200);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat
                .Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_logo)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.app_orange))
                .setContentTitle("У вас новое уведомление")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(uri)
                .setContentIntent(pIntent);
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Audio attributes
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            NotificationChannel channel = new NotificationChannel(channelId, "New Trip Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setDescription(messageBody);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.enableVibration(true);
            channel.setSound(uri, attributes);
            nm.createNotificationChannel(channel);
        }
        nm.notify(notificationId++, notificationBuilder.build());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            nm.createNotificationChannel(channel);
//        }
//
//        nm.notify(notificationId++, notificationBuilder.build());
    }
}

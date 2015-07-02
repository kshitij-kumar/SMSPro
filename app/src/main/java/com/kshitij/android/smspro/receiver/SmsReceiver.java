package com.kshitij.android.smspro.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.kshitij.android.smspro.R;
import com.kshitij.android.smspro.ui.SmsFeedActivity;
import com.kshitij.android.smspro.ui.SmsViewActivity;
import com.kshitij.android.smspro.util.SmsUtils;

/**
 * Created by kshitij.kumar on 30-06-2015.
 */

/**
 * An implementation of BroadcastReceiver, used to receive SMS
 */

public class SmsReceiver extends BroadcastReceiver {

    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    public static final int REQUEST_CODE_NOTIFICATION_LAUNCH = 555;
    private static final String TAG = SmsReceiver.class.getSimpleName();
    private static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage
                        .createFromPdu((byte[]) sms[i]);

                String message = smsMessage.getMessageBody().toString();
                String phoneNumber = smsMessage.getOriginatingAddress();
                SmsUtils.saveReceivedSms(context, phoneNumber, message);
                displayNotification(context, phoneNumber, message);
            }
        }
    }

    /*
     * Builds a notification, on clicking notification SmsViewActivity is
     * launched
     */
    private void displayNotification(Context context, String phoneNumber,
                                     String message) {
        int notificationId = 007;
        String title = SmsUtils.getContactName(context, phoneNumber);
        if (SmsUtils.isNullOrEmpty(title)) {
            title = phoneNumber;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title).setContentText(message);

        Intent viewIntent = new Intent(context, SmsViewActivity.class);
        viewIntent.putExtra(SmsViewActivity.EXTRA_PHONE_NUMBER, phoneNumber);
        viewIntent.putExtra(SmsViewActivity.EXTRA_MESSAGE, message);
        viewIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);

        Intent backIntent = new Intent(context, SmsFeedActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent viewPendingIntent = PendingIntent.getActivities(context, REQUEST_CODE_NOTIFICATION_LAUNCH,
                new Intent[]{backIntent, viewIntent}, PendingIntent.FLAG_ONE_SHOT);

        builder.setContentIntent(viewPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, builder.build());
    }
}
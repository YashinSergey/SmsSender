package com.example.smssender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import java.util.Objects;

public class SmsMonitor extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private String text;

    @Override
    public void onReceive(Context context, Intent intent) {
        getMessage(context, intent);
    }

    private void getMessage(Context context, Intent intent) {
        SmsMessage[] messages = null;
        String smsFrom = null;
        if (intent != null && intent.getAction() != null
                && SMS_RECEIVED.compareToIgnoreCase(intent.getAction()) == 0) {
            Object[] pduArr = (Object[]) Objects.requireNonNull(intent.getExtras()).get("pdus");
            assert pduArr != null;
            messages = new SmsMessage[pduArr.length];
            smsFrom = messages[0].getDisplayOriginatingAddress();
            for (int i = 0; i < pduArr.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArr[i]);
            }
        }

        assert smsFrom != null;
        if (smsFrom.equalsIgnoreCase(MainActivity.address)) {
            StringBuilder textBody = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                textBody.append(messages[i].getMessageBody());
            }
            text = textBody.toString();
            Intent secIntent = new Intent(context, MainActivity.class);
            secIntent.putExtra("sms_body", text);
            context.startService(secIntent);
        }
        abortBroadcast();
    }
}

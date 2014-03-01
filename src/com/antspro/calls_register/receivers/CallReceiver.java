package com.antspro.calls_register.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
    String phoneNumber = "";
    long startCallTime, endCallTime;
    SharedPreferences sp;

    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("com.exampe.calls_register", Context.MODE_PRIVATE);
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            //получаем исходящий номер
            phoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Log.d("askar", "NEW_OUTGOING_CALL - " + phoneNumber);
            startCallTime = System.currentTimeMillis();
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong("startCallTime", startCallTime);
            editor.commit();
        } else if (intent.getAction().equals("android.intent.action.PHONE_STATE")){
            String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //телефон звонит, получаем входящий номер
                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d("askar", "EXTRA_STATE_RINGING - " + phoneNumber);
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                //телефон находится в режиме звонка (набор номера / разговор)
                Log.d("askar", "EXTRA_STATE_OFFHOOK - " + phoneNumber);
            } else if (phone_state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                //телефон находиться в ждущем режиме. Это событие наступает по окончанию разговора, когда мы уже знаем номер и факт звонка
                Log.d("askar", "EXTRA_STATE_IDLE - " + phoneNumber);
                endCallTime = System.currentTimeMillis();
            }
        }
        if (endCallTime != 0){
            startCallTime = sp.getLong("startCallTime", 0);
            long s = endCallTime - startCallTime;
            long s1 = s / 1000;
            Log.d("askar", String.valueOf(s1));
        }
    }
}
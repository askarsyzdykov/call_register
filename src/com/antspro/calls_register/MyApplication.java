package com.antspro.calls_register;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import com.antspro.calls_register.receivers.SyncReceiver;
import com.ubertesters.sdk.Ubertesters;

/**
 * Created by arna on 05.03.14.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Ubertesters.initialize(this);
        Log.v("askarlog", "Application started");
        Context mContext = getApplicationContext();
        boolean alarmUp = (PendingIntent.getBroadcast(mContext, 0,
                new Intent(mContext, SyncReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUp)
        {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent1 = new Intent(mContext, SyncReceiver.class);
            PendingIntent pIntent1 = PendingIntent.getBroadcast(mContext, 0, intent1, 0);
            Log.d("askarlog", "starting alarm manager");
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime()+10000, 1000 * 60 * 60, pIntent1);  // 24 hours = 86400000;
        } else {
            Log.d("askarlog", "Alarm is already active");
        }
    }
}

package com.antspro.calls_register.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncReceiver extends BroadcastReceiver {
    final String LOG_TAG = "askar";

    public SyncReceiver(){
        Log.d(LOG_TAG, "ctor");
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Log.d(LOG_TAG, "onReceive");
    }
}
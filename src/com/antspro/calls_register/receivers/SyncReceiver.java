package com.antspro.calls_register.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.antspro.calls_register.controllers.StatisticController;
import com.antspro.calls_register.model.Statistic;

import java.util.ArrayList;
import java.util.logging.Logger;

public class SyncReceiver extends BroadcastReceiver {
    final String LOG_TAG = "askarlog";

    public SyncReceiver(){
        //Log.d(LOG_TAG, "ctor");
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        SharedPreferences sp = ctx.getSharedPreferences("com.exampe.calls_register", Context.MODE_PRIVATE);
        boolean isActive = sp.getBoolean("sync_active", true);
        String serverUrl = sp.getString("server_url","") + "/export.json";
        String username = sp.getString("username","");
        String password = sp.getString("password","");
        if (TextUtils.isEmpty(serverUrl))
        {
            Log.d(LOG_TAG, "no server url");
            return;
        }
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
        {
            Log.d(LOG_TAG, "no username/password");
            return;
        }

        if (isActive)
            new PushStatisticToServerAsyncTask(ctx).execute();
        else
            Log.d(LOG_TAG, "sync off");
    }

    public class PushStatisticToServerAsyncTask extends AsyncTask<ArrayList<Statistic>, Void, Boolean> {
        private Context mContext;

        public PushStatisticToServerAsyncTask(Context context) {
            this.mContext = context;
            Log.d(LOG_TAG, "PushStatisticToServerAsyncTask ctor");
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }

        @Override
        protected Boolean doInBackground(ArrayList<Statistic>... args) {
            StatisticController.initStatistics(mContext);
            StatisticController controller = new StatisticController(mContext);
            ArrayList<Statistic> list = controller.getNotPostedToServerStatistics();
            try {
                if (controller.postStatistics(list)){
                    for (Statistic statistic : list){
                        statistic.setPostedToServer(true);
                        controller.insertStatistic(statistic);
                    }
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, e != null ? e.getMessage() : "unknown error");
                return false;
            }
            return true;
        }
    }
}
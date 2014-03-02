package com.antspro.calls_register.controllers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import com.antspro.calls_register.db.StatisticTable;
import com.antspro.calls_register.model.Statistic;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StatisticController {

    StatisticTable rDb;

    public StatisticController(Context c) {
        rDb = new StatisticTable(c);
    }

    public long insertStatistic(Statistic statistic) {
        long result;
        rDb.open();
        Date d = statistic.getDate();
        statistic.setDate(new Date(d.getYear(), d.getMonth(), d.getDate()));
        result = rDb.updateStatistic(statistic.getCallsCount(), statistic.getDuration(), statistic.getDate(),
                statistic.isPostedToServer());
        if (result == 0)
        {
            result = rDb.putStatistic(statistic.getCallsCount(), statistic.getDuration(), statistic.getDate(),
                statistic.isPostedToServer());
        }
        rDb.close();
        return result;
    }

    public ArrayList<Statistic> getStatistics() {
        ArrayList<Statistic> statistics = new ArrayList<Statistic>();
        rDb.openRead();
        Cursor cursor = rDb.getStatistic();
        while (!cursor.isAfterLast()) {
            statistics.add(new Statistic(cursor.getInt(2), cursor.getInt(3), new Date(cursor.getLong(4)),
                    cursor.getInt(5) == 1));
            cursor.moveToNext();
        }
        cursor.close();
        rDb.close();
        return statistics;
    }


    public static JSONObject postStatistic(String url, Statistic statistic) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        StringEntity se = null;
        JSONObject json = new JSONObject();
        // String responseBody = null;
        JSONObject retVal = new JSONObject();
        try {
            json.put("duration", statistic.getDuration());
            json.put("calls_count", statistic.getCallsCount());
            json.put("date", statistic.getDate());
            se = new StringEntity(json.toString());
            httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            HttpResponse response = httpclient.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            retVal = new JSONObject(reader.readLine());
            return retVal;
        } catch (Exception e) {
            Log.e("ssp", e.getMessage());
        }
        return retVal;
    }

    public static void initStatistics(Context context) {
        StatisticController controller = new StatisticController(context);
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null,
                CallLog.Calls.DATE + " ASC");
        int numberColumn = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int typeColumn = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int dateColumn = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int durationColumn = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        Date tempDate = null;
        Statistic statistic = null;
        try {
            tempDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse("01/02/2014");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(numberColumn);
            String callType = managedCursor.getString(typeColumn);
            String callDate = managedCursor.getString(dateColumn);
            Date callDayTime = new Date(Long.valueOf(callDate));
            int callDuration = Integer.parseInt(managedCursor.getString(durationColumn));
            if (callDuration == 0) continue;
            if (CallLog.Calls.OUTGOING_TYPE != Integer.parseInt(callType)) continue;
            if (callDayTime.getTime() >= tempDate.getTime()){
                if (statistic == null || callDayTime.getDate() != tempDate.getDate() || callDayTime.getMonth() != tempDate.getMonth()
                        || callDayTime.getYear() != tempDate.getYear()){
                    tempDate = callDayTime;
                    if (statistic != null){
                        controller.insertStatistic(statistic);
                    }
                    statistic = new Statistic(0, 0, tempDate, false);
                }
                if (callDuration > 0) {
                    statistic.incCallsCount(1);
                    statistic.incDuration(callDuration);
                }
            }
        }
        managedCursor.close();
        if (statistic != null)
            controller.insertStatistic(statistic);
    }
}

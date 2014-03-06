package com.antspro.calls_register.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import com.antspro.calls_register.db.StatisticTable;
import com.antspro.calls_register.model.Statistic;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatisticController {
    final static String SALT = "12345678";
    StatisticTable rDb;
    String mUsername, mPassword;
    Context mContext;

    public StatisticController(Context c) {
        rDb = new StatisticTable(c);
        mContext = c;
        SharedPreferences sp = c.getSharedPreferences("com.exampe.calls_register", Context.MODE_PRIVATE);
        mUsername = sp.getString("username","");
        mPassword = sp.getString("password","");
    }

    public long insertStatistic(Statistic statistic) {
        Date d = statistic.getDate();
        statistic.setDate(new Date(d.getYear(), d.getMonth(), d.getDate()));
        Statistic s = getStatisticForDate(statistic.getDate());
        if (s != null && s.getCallsCount() == statistic.getCallsCount() && s.getDuration() == s.getDuration() &&
                s.isPostedToServer()){
            return 0;
        }
        long result;
        rDb.open();
        result = rDb.updateStatistic(statistic.getCallsCount(), statistic.getDuration(), statistic.getDate(),
                statistic.isPostedToServer());
        if (result == 0)
        {
            Log.v("askar", "statistic created");
            result = rDb.putStatistic(statistic.getCallsCount(), statistic.getDuration(), statistic.getDate(),
                    statistic.isPostedToServer());
        } else {
            Log.v("askar", "statistic updated");
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

    public Statistic getStatisticForDate(Date d) {
        Statistic statistic = null;
        rDb.openRead();
        Cursor cursor = rDb.getStatisticForDate(d);
        if (cursor != null && cursor.getCount() > 0){
            statistic = new Statistic(cursor.getInt(2), cursor.getInt(3), new Date(cursor.getLong(4)),
                    cursor.getInt(5) == 1);
        }
        cursor.close();
        rDb.close();
        return statistic;
    }

    public ArrayList<Statistic> getNotPostedToServerStatistics() {
        ArrayList<Statistic> statistics = new ArrayList<Statistic>();
        rDb.openRead();
        Cursor cursor = rDb.getNotPostedToServerStatistics();
        while (!cursor.isAfterLast()) {
            statistics.add(new Statistic(cursor.getInt(2), cursor.getInt(3), new Date(cursor.getLong(4)),
                    cursor.getInt(5) == 1));
            cursor.moveToNext();
        }
        cursor.close();
        rDb.close();
        return statistics;
    }

//    public JSONObject postStatistic(Statistic statistic) {
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost(SERVER_URL);
//        StringEntity se = null;
//        JSONObject json = new JSONObject();
//        // String responseBody = null;
//        JSONObject retVal = new JSONObject();
//        try {
//            json.put("duration", statistic.getDuration());
//            json.put("calls_count", statistic.getCallsCount());
//            json.put("date", statistic.getDate());
//            se = new StringEntity(json.toString());
//            httpPost.setEntity(se);
//            httpPost.setHeader("Content-type", "application/json");
//            httpPost.setHeader("Accept", "application/json");
//            HttpResponse response = httpclient.execute(httpPost);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//            retVal = new JSONObject(reader.readLine());
//            return retVal;
//        } catch (Exception e) {
//            Log.e("ssp", e.getMessage());
//        }
//        return retVal;
//    }

    public boolean postStatistics(ArrayList<Statistic> statistics) throws Exception {
        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)){
            throw new Exception("Не указаны логин или пароль");
        }
        SharedPreferences sp =  mContext.getSharedPreferences("com.exampe.calls_register", Context.MODE_PRIVATE);
        String serverUrl = sp.getString("server_url","") + "/export.json";
        if (statistics.size() == 0) return false;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(serverUrl);
        StringEntity se;
        JSONObject json = new JSONObject();
        JSONArray jsonStatistics = new JSONArray();
        // String responseBody = null;
        JSONObject retVal = new JSONObject();
        try {
            json.put("login", mUsername);
            json.put("password", mPassword);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Statistic statistic : statistics){
                JSONObject jsonStat = new JSONObject();
                jsonStat.put("count", statistic.getCallsCount());
                jsonStat.put("duration", statistic.getDuration());
                String dateStr = dateFormat.format(statistic.getDate());
                jsonStat.put("day", dateStr);
                String str = String.format("%s#%s#%s#%s", dateStr, statistic.getCallsCount(), statistic.getDuration(), SALT);
                jsonStat.put("crc", md5(str));
                jsonStatistics.put(jsonStat);
            }
            json.put("statistic", jsonStatistics);
            se = new StringEntity(json.toString());
            httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            HttpResponse response = httpclient.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            retVal = new JSONObject(reader.readLine());
            return retVal.getBoolean("success");
        } catch (Exception e) {
            Log.e("ssp", e.getMessage());
            return false;
        }
    }

    public String md5(String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static void initStatistics(Context context) {
        Date now = new Date();
        StatisticController controller = new StatisticController(context);
        StringBuffer sb = new StringBuffer();
        Date d = addDays(now, -1);
        d.setHours(0);
        d.setMinutes(0);
        d.setMinutes(0);
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                String.format("%s = 2 AND %s > 0 AND %s >= ?", CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE),
                new String[]{String.valueOf(d.getTime())},
                CallLog.Calls.DATE + " ASC");
        int dateColumn = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int durationColumn = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        Date tempDate = null;
        Statistic statistic = null;
        tempDate = new Date(now.getYear(), now.getMonth(), now.getDate()-1); //new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse("01/02/2014");
        while (managedCursor.moveToNext()) {
            String callDate = managedCursor.getString(dateColumn);
            Date callDayTime = new Date(Long.valueOf(callDate));
            int callDuration = Integer.parseInt(managedCursor.getString(durationColumn));
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

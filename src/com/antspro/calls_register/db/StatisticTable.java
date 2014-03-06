package com.antspro.calls_register.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.Date;

public class StatisticTable extends ProjectDB {

    public StatisticTable(Context c) {
        super(c);
    }

    public long putStatistic(int count, int duration, Date date, boolean isPostedToServer) {
        ContentValues cv = new ContentValues();
        cv.put(STAT_CALLS_COUNT, count);
        cv.put(STAT_CALLS_DURATION, duration);
        cv.put(STAT_DATE, (new Date(date.getYear(), date.getMonth(), date.getDate())).getTime());
        cv.put(STAT_POSTED_TO_SERVER, isPostedToServer ? 1 : 0);
        return mDb.insert(STATISTIC_TABLE, null, cv);
    }

    public long updateStatistic(int count, int duration, Date date, boolean isPostedToServer) {
        long dateInMillis = (new Date(date.getYear(), date.getMonth(), date.getDate())).getTime();
        ContentValues cv = new ContentValues();
        cv.put(STAT_CALLS_COUNT, count);
        cv.put(STAT_CALLS_DURATION, duration);
        cv.put(STAT_DATE, dateInMillis);
        cv.put(STAT_POSTED_TO_SERVER, isPostedToServer ? 1 : 0);
        return mDb.update(STATISTIC_TABLE, cv, STAT_DATE + " = \"" + dateInMillis + "\"", null);
    }

    public long updateStatistic(Date date, boolean isPostedToServer) {
        long dateInMillis = (new Date(date.getYear(), date.getMonth(), date.getDate())).getTime();
        ContentValues cv = new ContentValues();
        cv.put(STAT_DATE, dateInMillis);
        cv.put(STAT_POSTED_TO_SERVER, isPostedToServer ? 1 : 0);
        return mDb.update(STATISTIC_TABLE, cv, STAT_DATE + " = \"" + dateInMillis + "\"", null);
    }

    public Cursor getStatistic() {
        Cursor cursor = mDb.rawQuery(
                String.format("SELECT * FROM %s ORDER BY %s ASC", STATISTIC_TABLE, STAT_DATE),
                null);
        if (cursor != null && cursor.getCount() != 0)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getStatisticForDate(Date d) {
        Cursor cursor = mDb.rawQuery(
                String.format("SELECT * FROM %s WHERE %s = %s", STATISTIC_TABLE, STAT_DATE, d.getTime()),
                null);
        if (cursor != null && cursor.getCount() != 0)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getNotPostedToServerStatistics() {
        Cursor cursor = mDb.rawQuery(
                String.format("SELECT * FROM %s WHERE %s = 0 ORDER BY %s ASC", STATISTIC_TABLE, STAT_POSTED_TO_SERVER, STAT_DATE),
                null);
        if (cursor != null && cursor.getCount() != 0)
            cursor.moveToFirst();
        return cursor;
    }
}

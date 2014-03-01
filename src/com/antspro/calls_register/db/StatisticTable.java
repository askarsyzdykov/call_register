package com.antspro.calls_register.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class StatisticTable extends ProjectDB {

    public StatisticTable(Context c) {
        super(c);
    }

    public long putStatistic(int count, int duration, String date, boolean isPostedToServer) {
        ContentValues cv = new ContentValues();
        cv.put(STAT_CALLS_COUNT, count);
        cv.put(STAT_CALLS_DURATION, duration);
        cv.put(STAT_DATE, date);
        cv.put(STAT_POSTED_TO_SERVER, isPostedToServer ? 1 : 0);
        return mDb.insert(STATISTIC_TABLE, null, cv);
    }

    public long updateStatistic(int count, int duration, String date, boolean isPostedToServer) {
        ContentValues cv = new ContentValues();
        cv.put(STAT_CALLS_COUNT, count);
        cv.put(STAT_CALLS_DURATION, duration);
        cv.put(STAT_DATE, date);
        cv.put(STAT_POSTED_TO_SERVER, isPostedToServer ? 1 : 0);
        return mDb.update(STATISTIC_TABLE, cv, STAT_DATE + " = \"" + date + "\"", null);
    }

    public Cursor getStatistic() {
        Cursor cursor = mDb.rawQuery(
                String.format("SELECT * FROM %s", STATISTIC_TABLE),
                null);
        if (cursor != null && cursor.getCount() != 0)
            cursor.moveToFirst();
        return cursor;
    }
}

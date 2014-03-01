package com.antspro.calls_register.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class ProjectDB {
	protected static final String DATABASE_NAME = "ProjectDB";
	protected static int DATABASE_VERSION = 2;
	protected static final String TAG = "ssp";

    protected static final String STATISTIC_TABLE = "StatisticTable";
    protected static final String STAT_ID = "Id";
    protected static final String STAT_DATE = "Date";
    protected static final String STAT_CALLS_COUNT = "Count";
    protected static final String STAT_CALLS_DURATION = "Duration";
    protected static final String STAT_POSTED_TO_SERVER = "IsPostedToServer";

	protected final Context mCtx;
	protected static DBHelper mDbHelper;
	protected static SQLiteDatabase mDb;

	protected class DBHelper extends SQLiteOpenHelper {
        private final String CREATE_TABLE_TRACKMANS = String.format(
                "CREATE TABLE %s (_id integer primary key autoincrement,"
                        + " %s integer, %s integer, %s integer, %s text, %s integer)", STATISTIC_TABLE, STAT_ID,
                 STAT_CALLS_DURATION, STAT_CALLS_COUNT, STAT_DATE, STAT_POSTED_TO_SERVER);

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if (db != null) {
                db.execSQL(CREATE_TABLE_TRACKMANS);
			} else {
				Log.e(TAG, "SQLite ERROR: Couldn't create tables for database "
                        + DATABASE_NAME);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (db != null) {
				Log.w(TAG,
                        "Upgrading database, which will destroy all old data from tables ");
                db.execSQL(String.format("DROP TABLE IF EXISTS %s", STATISTIC_TABLE));
				onCreate(db);
			} else {
				Log.e(TAG,
                        "SQLite ERROR: Couldn't upgrade tables from database "
                                + DATABASE_NAME);
			}
		}
	}

	public ProjectDB(Context c) {
		this.mCtx = c;
	}

	public ProjectDB open() throws SQLException {
		mDbHelper = new DBHelper(mCtx);
		try {
			mDb = mDbHelper.getWritableDatabase();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return this;
	}

	public ProjectDB openRead() throws SQLException {
		mDbHelper = new DBHelper(mCtx);
		mDb = mDbHelper.getReadableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}
}

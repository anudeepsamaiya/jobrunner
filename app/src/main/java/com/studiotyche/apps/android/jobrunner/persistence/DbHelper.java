package com.studiotyche.apps.android.jobrunner.persistence;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.studiotyche.apps.android.jobrunner.Alert;

import java.util.ArrayList;


/**
 * Created by AnudeepSamaiya on 01-10-2015.
 */

public final class DbHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String TAG = "DbHelper";

    private static final String DB_NAME = "gabja";
    private static final String DB_SUFFIX = ".db";
    private static final int DB_VERSION = 1;

    private static DbHelper mInstance;

    private DbHelper(Context context) {
        super(context, DB_NAME + DB_SUFFIX, null, DB_VERSION);
        this.context = context;
    }

    public static DbHelper getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new DbHelper(context.getApplicationContext());
            Log.i(TAG, "Created a new Instance");
        }
        Log.i(TAG, "Returning current Instance");
        return mInstance;
    }

    public void addSavedFeedRecord(Alert alert) {

        String title, description, link, timestamp;
        title = alert.getTitle();
        description = alert.getDesc();
        link = alert.getLink();
        timestamp = alert.getTimeStamp();

        Log.i("Joberio","DBHelper: "+title+" "+description+" "+link+" "+timestamp);

        SQLiteDatabase sqliteDatabase = getWritableDatabase(context);
        sqliteDatabase.beginTransaction();

        ContentValues cv = new ContentValues();
        cv.put(SavedFeedTable.COLUMN_TITLE, title);
        cv.put(SavedFeedTable.COLUMN_DESCRIPTION, description);
        cv.put(SavedFeedTable.COLUMN_LINK, link);
        cv.put(SavedFeedTable.COLUMN_TIMESTAMP, timestamp);

        sqliteDatabase.insert(SavedFeedTable.NAME, null, cv);
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();

        Log.d(TAG, "Saved new alert " + title);
    }

    public void addNewAlert(Alert alert) {
        String title, description, link, timestamp;
        title = alert.getTitle();
        description = alert.getDesc();
        link = alert.getLink();
        timestamp = alert.getTimeStamp();

        SQLiteDatabase sqliteDatabase = getWritableDatabase(context);
        sqliteDatabase.beginTransaction();

        ContentValues cv = new ContentValues();
        cv.put(AlertFeedTable.COLUMN_TITLE, title);
        cv.put(AlertFeedTable.COLUMN_DESCRIPTION, description);
        cv.put(AlertFeedTable.COLUMN_LINK, link);
        cv.put(AlertFeedTable.COLUMN_TIMESTAMP, timestamp);

        sqliteDatabase.insert(AlertFeedTable.NAME, null, cv);
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();

        Log.d(TAG, "Added new alert " + title);

    }

    public ArrayList<Alert> getAllAlerts() {
        String title, description, link, timestamp;
        ArrayList<Alert> allAlerts = new ArrayList<>();

        SQLiteDatabase sqliteDatabase = getReadableDatabase(context);
        Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM " + AlertFeedTable.NAME + "ORDER BY "
                + AlertFeedTable.COLUMN_TIMESTAMP + " DESC", null);

        Log.d(TAG + " getALlAlerts()", "Cursor size " + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                title = cursor.getString(cursor.getColumnIndex(AlertFeedTable.COLUMN_TITLE));
                description = cursor.getString(cursor.getColumnIndex(AlertFeedTable.COLUMN_DESCRIPTION));
                link = cursor.getString(cursor.getColumnIndex(AlertFeedTable.COLUMN_LINK));
                timestamp = cursor.getString(cursor.getColumnIndex(AlertFeedTable.COLUMN_TIMESTAMP));

                allAlerts.add(new Alert(title, description, link, timestamp));
                Log.d(TAG + " getALlAlerts()", "added new alert to allalerts");
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqliteDatabase.close();
        Log.d(TAG + "getALlAlerts()", "Returning allalerts " + allAlerts.size());
        return allAlerts;
    }

    public ArrayList<Alert> getAllSavedAlerts() {
        String title, description, link, timestamp;
        ArrayList<Alert> allAlerts = new ArrayList<>();

        SQLiteDatabase sqliteDatabase = getReadableDatabase(context);
        Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM " + SavedFeedTable.NAME + "ORDER BY "
                + SavedFeedTable.COLUMN_TIMESTAMP + " DESC", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {

                title = cursor.getString(cursor.getColumnIndex(SavedFeedTable.COLUMN_TITLE));
                description = cursor.getString(cursor.getColumnIndex(SavedFeedTable.COLUMN_DESCRIPTION));
                link = cursor.getString(cursor.getColumnIndex(SavedFeedTable.COLUMN_LINK));
                timestamp = cursor.getString(cursor.getColumnIndex(SavedFeedTable.COLUMN_TIMESTAMP));

                allAlerts.add(new Alert(title, description, link, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqliteDatabase.close();
        return allAlerts;
    }

    public boolean checkIfTableExists(String tableName) {
        boolean tableExists = false;

        SQLiteDatabase sqliteDatabase = getReadableDatabase(context);
        Cursor cursor = sqliteDatabase.rawQuery("Select * from " + tableName, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                tableExists = true;
            }
        }
        cursor.close();
        sqliteDatabase.close();
        return tableExists;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SavedFeedTable.CREATE);
        Log.i(TAG, " Table Created savedfeedtable ");
        db.execSQL(AlertFeedTable.CREATE);
        Log.i(TAG, " Table Created alertfeedtable ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }
}
